# Use Python 3.10 slim image
FROM python:3.10-slim

# Set environment variables with modern syntax
ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1 \
    PORT=8080

# Set working directory
WORKDIR /app

# Install system dependencies including GDAL
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
    && rm -rf /var/lib/apt/lists/*

# Set GDAL environment variables
ENV CPLUS_INCLUDE_PATH=/usr/include/gdal \
    C_INCLUDE_PATH=/usr/include/gdal \
    GDAL_LIBRARY_PATH=/usr/lib/libgdal.so \
    GEOS_LIBRARY_PATH=/usr/lib/libgeos_c.so

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