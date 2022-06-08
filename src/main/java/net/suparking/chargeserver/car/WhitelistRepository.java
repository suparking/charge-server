package net.suparking.chargeserver.car;

public interface WhitelistRepository {
    boolean isInWhitelist(String plateNo);
}
