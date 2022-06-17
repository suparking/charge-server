package net.suparking.chargeserver.parking.mysql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo implements Serializable {

    /**
     * 用户手机号.
     */
    private String phone;

    /**
     * 用户在小程序的OpenID.
     */
    private String miniOpenId;

    // 用户ID.
    private String userId;

}
