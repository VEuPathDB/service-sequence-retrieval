#!/usr/bin/env sh

function await() {
  sleeps=0

  echo "Waiting for $1 to become available"
  while ! nc -zv $1 $2; do
    if [ $sleeps -gt 5 ]; then
      echo "$1 took too long to become available, stopping now"
      exit 1
    fi

    sleep 1

    sleeps=$((sleeps+1))
  done

  echo "$1 is available"
}

echo "Awaiting service dependencies."

echo "Testing connectivity to job queue 1"
await "${JOB_QUEUE_HOST}" "${JOB_QUEUE_PORT}" || exit 1

echo "Testing connectivity to PostgreSQL"
await "${QUEUE_DB_HOST}" "${QUEUE_DB_PORT}" || exit 1

echo "Testing connectivity to Minio"
await "${S3_HOST}" "${S3_PORT}" || exit 1

java -jar -XX:+CrashOnOutOfMemoryError $JVM_MEM_ARGS $JVM_ARGS /service.jar
