# Use Python 3.10 slim image
FROM python:3.10-slim

# Set environment variables
ENV PYTHONDONTWRITEBYTECODE 1
ENV PYTHONUNBUFFERED 1
ENV PORT 8080

# Set working directory
WORKDIR /app

# Install system dependencies including GDAL and GEOS
RUN apt-get update && apt-get install -y \
    gcc \
    libpq-dev \
    postgresql-client \
    netcat-traditional \
    gdal-bin \
    libgdal-dev \
    libgeos-dev \
    && rm -rf /var/lib/apt/lists/*

# Set GDAL and GEOS environment variables
ENV GDAL_LIBRARY_PATH=/usr/lib/libgdal.so
ENV GEOS_LIBRARY_PATH=/usr/lib/libgeos_c.so

# Install Python dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy the entire project
COPY . .

# Create and set permissions for the entrypoint script
COPY entrypoint.sh .
RUN chmod +x entrypoint.sh

# Collect static files
RUN python manage.py collectstatic --noinput

# Expose the port that Cloud Run expects
EXPOSE 8080

# Use entrypoint script
ENTRYPOINT ["./entrypoint.sh"]