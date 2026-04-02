## Kafka DLQ replay (local docker-compose)

This project uses:

- **Main topic**: `tms.notifications.v1`
- **DLQ topic**: `tms.notifications.dlq.v1`

When a consumer fails after retries, the message is sent to DLQ.

### 1) Inspect DLQ messages

From PowerShell (project root):

```powershell
docker compose exec kafka bash -lc "kafka-console-consumer --bootstrap-server kafka:9092 --topic tms.notifications.dlq.v1 --from-beginning --property print.key=true --property print.value=true --timeout-ms 10000"
```

### 2) Replay DLQ back to main topic

This re-publishes the **value** back to the main topic (and keeps per-user ordering by using the message key).

```powershell
docker compose exec kafka bash -lc "kafka-console-consumer --bootstrap-server kafka:9092 --topic tms.notifications.dlq.v1 --from-beginning --property print.key=true --property print.value=true --timeout-ms 10000 | awk -F $'\t' '{print \$1 \"\t\" \$2}' | kafka-console-producer --bootstrap-server kafka:9092 --topic tms.notifications.v1 --property parse.key=true --property key.separator=$'\t'"
```

Notes:

- This is meant for **local learning**. For production you would implement controlled replay tooling.
- The command replays **all** messages read by the consumer command; adjust `--from-beginning` and `--timeout-ms` for smaller batches.

