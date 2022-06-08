package net.suparking.chargeserver.car;

import net.suparking.server.ServerApplication;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "blacklist")
public class Blacklist {
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

    public static boolean isInBlacklist(String plateNo) {
        return blacklistRepository.isInBlacklist(plateNo);
    }

    private static BlacklistRepository blacklistRepository = ServerApplication.getBean(
            "BlacklistRepositoryImpl", BlacklistRepository.class);

    @Override
    public String toString() {
        return "Blacklist{" + "id=" + id + ", plateNo='" + plateNo + '\'' + ", beginDate=" + beginDate + ", endDate=" +
               endDate + ", remark='" + remark + '\'' + ", imageURL='" + imageURL + '\'' + ", projectNo='" + projectNo +
               '\'' + ", creator='" + creator + '\'' + ", createTime='" + createTime + '\'' + ", modifier='" +
               modifier + '\'' + ", modifyTime='" + modifyTime + '\'' + '}';
    }
}
