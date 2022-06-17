package net.suparking.chargeserver.parking.mysql;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * BaseDo.
 */
@Data
public class BaseDO implements Serializable {

    private static final long serialVersionUID = 9183753993391891137L;

    private Long id;

    private Timestamp dateCreated;

    private Timestamp dateUpdated;
}
