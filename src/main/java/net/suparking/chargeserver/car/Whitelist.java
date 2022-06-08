package net.suparking.chargeserver.car;

import net.suparking.server.ServerApplication;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "whitelist")
public class Whitelist {
    @Id
    public ObjectId id;
    public String plateNo;
    public Long beginDate;
    public Long endDate;
    public String remark;
    public String imageURL;
    public String projectNo;
    public String creator;
    public String createTime;
    public String modifier;
    public String modifyTime;

    public static boolean isInWhitelist(String plateNo) {
        return whitelistRepository.isInWhitelist(plateNo);
    }

    private static WhitelistRepository whitelistRepository = ServerApplication.getBean(
            "WhitelistRepositoryImpl", WhitelistRepository.class);

    @Override
    public String toString() {
        return "Whitelist{" + "id=" + id + ", plateNo='" + plateNo + '\'' + ", beginDate=" + beginDate + ", endDate=" +
               endDate + ", remark='" + remark + '\'' + ", imageURL='" + imageURL + '\'' + ", projectNo='" + projectNo +
               '\'' + ", creator='" + creator + '\'' + ", createTime='" + createTime + '\'' + ", modifier='" +
               modifier + '\'' + ", modifyTime='" + modifyTime + '\'' + '}';
    }
}
