package org.javaboy.pagoda.common.utils.uuid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SnowflakeIdWorker {
        private final long twepoch = 1288834974657L;
        private final long workerIdBits = 5L;
        private final long dataCenterIdBits = 5L;
        private final long maxWorkerId = ~(-1L << workerIdBits);
        private final long maxDataCenterId = ~(-1L << dataCenterIdBits);
        private final long sequenceBits = 12L;
        private final long workerIdShift = sequenceBits;
        private final long dataCenterIdShift = sequenceBits + workerIdBits;
        private final long timestampLeftShift = sequenceBits + workerIdBits + dataCenterIdBits;
        private final long sequenceMask = ~(-1L << sequenceBits);
        private final long workerId;
        private final long dataCenterId;
        private long sequence = 0L;
        private long lastTimestamp = -1L;

        public  SnowflakeIdWorker(@Value("${snowflake.workerId}") long workerId, @Value("${snowflake.dataCenterId}")  long dataCenterId) {
                if (workerId > maxWorkerId || workerId < 0) {
                        throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
                }
                if (dataCenterId > maxDataCenterId || dataCenterId < 0) {
                        throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDataCenterId));
                }
                this.workerId = workerId;
                this.dataCenterId = dataCenterId;
        }

        public synchronized long nextId() {
                long timestamp = timeGen();

                if (timestamp < lastTimestamp) {
                        throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
                }

                if (lastTimestamp == timestamp) {
                        sequence = (sequence + 1) & sequenceMask;
                        if (sequence == 0) {
                                timestamp = tilNextMillis(lastTimestamp);
                        }
                } else {
                        sequence = 0L;
                }

                lastTimestamp = timestamp;
                return ((timestamp - twepoch) << timestampLeftShift) | (dataCenterId << dataCenterIdShift) | (workerId << workerIdShift) | sequence;
        }

        private long tilNextMillis(long lastTimestamp) {
                long timestamp = timeGen();
                while (timestamp <= lastTimestamp) {
                        timestamp = timeGen();
                }
                return timestamp;
        }

        private long timeGen() {
                return System.currentTimeMillis();
        }
}
