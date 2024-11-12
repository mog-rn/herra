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
RUN sh -c 'echo "deb http://apt.postgresql.org/pub/repos/apt $(lsb_release -cs)-pgdg main" > /etc/apt/sources.list.d/pgdg.list' \
    && curl -fsSL https://www.postgresql.org/media/keys/ACCC4CF8.asc | gpg --dearmor -o /etc/apt/trusted.gpg.d/postgresql.gpg

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

# Create necessary directories and verify GDAL installation
RUN mkdir -p /usr/lib && \
    gdal-config --version && \
    # Find actual GDAL and GEOS library paths
    export GDAL_SO=$(find /usr/lib -name "libgdal.so.*" | head -n 1) && \
    export GEOS_SO=$(find /usr/lib -name "libgeos_c.so.*" | head -n 1) && \
    # Create symlinks if libraries exist
    if [ ! -z "$GDAL_SO" ]; then \
        ln -sf $GDAL_SO /usr/lib/libgdal.so; \
    fi && \
    if [ ! -z "$GEOS_SO" ]; then \
        ln -sf $GEOS_SO /usr/lib/libgeos_c.so; \
    fi && \
    # Verify links
    ls -l /usr/lib/libgdal.so /usr/lib/libgeos_c.so

# Set GDAL environment variables
ENV GDAL_VERSION=3.4.1
ENV CPLUS_INCLUDE_PATH=/usr/include/gdal \
    C_INCLUDE_PATH=/usr/include/gdal

# Install Python dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy the entire project
COPY . .

# Create static directories
RUN mkdir -p static staticfiles

# Set permissions for the entrypoint script
RUN chmod +x entrypoint.sh

# Collect static files with proper environment
ENV DJANGO_SETTINGS_MODULE=config.settings \
    DEBUG=True \
    SECRET_KEY=dummy-key-for-collectstatic \
    DB_HOST=dummy \
    DB_NAME=dummy \
    DB_USER=dummy \
    DB_PASSWORD=dummy \
    DB_PORT=5432

RUN python manage.py collectstatic --noinput --clear

# Expose the port that Cloud Run expects
EXPOSE 8080

# Use entrypoint script
ENTRYPOINT ["./entrypoint.sh"]