# Use Python 3.10 slim image
FROM python:3.10-slim

# Set environment variables with modern syntax
ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1 \
    PORT=8080

# Set working directory
WORKDIR /app

# Install system dependencies and GDAL
RUN apt-get update && apt-get install -y \
    software-properties-common \
    gnupg \
    wget \
    && rm -rf /var/lib/apt/lists/*

# Add UbuntuGIS PPA for GDAL 3.10
RUN wget -qO - https://apt.postgresql.org/pub/repos/apt/ACCC4CF8.asc | apt-key add - \
    && echo "deb http://apt.postgresql.org/pub/repos/apt/ bullseye-pgdg main" > /etc/apt/sources.list.d/pgdg.list \
    && apt-get update

# Install GDAL and other dependencies
RUN apt-get update && apt-get install -y \
    gcc \
    libpq-dev \
    postgresql-client \
    netcat-traditional \
    gdal-bin \
    libgdal-dev \
    python3-gdal \
    binutils \
    libproj-dev \
    libgeos-dev \
    libsqlite3-mod-spatialite \
    && rm -rf /var/lib/apt/lists/*

# Set GDAL environment variables
ENV CPLUS_INCLUDE_PATH=/usr/include/gdal \
    C_INCLUDE_PATH=/usr/include/gdal \
    GDAL_LIBRARY_PATH=/usr/lib/x86_64-linux-gnu/libgdal.so \
    GEOS_LIBRARY_PATH=/usr/lib/x86_64-linux-gnu/libgeos_c.so

# Verify GDAL installation
RUN gdal-config --version

# Install Python dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy the entire project
COPY . .

# Set permissions for the entrypoint script
RUN chmod +x entrypoint.sh

# Create static directory
RUN mkdir -p staticfiles

# Collect static files
RUN python manage.py collectstatic --noinput || true

# Expose the port that Cloud Run expects
EXPOSE 8080

# Use entrypoint script
ENTRYPOINT ["./entrypoint.sh"]