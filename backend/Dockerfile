# Use Python 3.10 slim image
FROM python:3.10-slim

# Set environment variables with modern syntax
ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1 \
    PORT=8080

# Set working directory
WORKDIR /app

# Install build dependencies
RUN apt-get update && apt-get install -y \
    curl \
    gnupg2 \
    software-properties-common \
    && rm -rf /var/lib/apt/lists/*

# Add PostgreSQL repository
RUN curl https://www.postgresql.org/media/keys/ACCC4CF8.asc | apt-key add - \
    && echo "deb http://apt.postgresql.org/pub/repos/apt $(lsb_release -cs)-pgdg main" > /etc/apt/sources.list.d/pgdg.list

# Install main dependencies
RUN apt-get update && apt-get install -y \
    gcc \
    g++ \
    libpq-dev \
    postgresql-client-15 \
    netcat-traditional \
    binutils \
    gdal-bin \
    libgdal-dev \
    python3-gdal \
    libproj-dev \
    libgeos-dev \
    libspatialite-dev \
    && rm -rf /var/lib/apt/lists/*

# Set GDAL environment variables
ENV GDAL_VERSION=3.4.1
ENV CPLUS_INCLUDE_PATH=/usr/include/gdal \
    C_INCLUDE_PATH=/usr/include/gdal \
    GDAL_LIBRARY_PATH=/usr/lib/x86_64-linux-gnu/libgdal.so.31 \
    GEOS_LIBRARY_PATH=/usr/lib/x86_64-linux-gnu/libgeos_c.so.1

# Create symbolic link for GDAL
RUN ln -s /usr/lib/x86_64-linux-gnu/libgdal.so.* /usr/lib/libgdal.so \
    && ln -s /usr/lib/x86_64-linux-gnu/libgeos_c.so.* /usr/lib/libgeos_c.so

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

# Collect static files without database connection
ENV DJANGO_SETTINGS_MODULE=config.settings
ENV DEBUG=True
ENV SECRET_KEY=dummy-key-for-collectstatic
ENV DB_HOST=dummy
ENV DB_NAME=dummy
ENV DB_USER=dummy
ENV DB_PASSWORD=dummy
ENV DB_PORT=5432

RUN python manage.py collectstatic --noinput || true

# Expose the port that Cloud Run expects
EXPOSE 8080

# Use entrypoint script
ENTRYPOINT ["./entrypoint.sh"]