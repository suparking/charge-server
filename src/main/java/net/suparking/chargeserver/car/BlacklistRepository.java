package net.suparking.chargeserver.car;

public interface BlacklistRepository {
    boolean isInBlacklist(String plateNo);
}
