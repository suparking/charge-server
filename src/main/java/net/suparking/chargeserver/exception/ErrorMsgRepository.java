package net.suparking.chargeserver.exception;

import net.suparking.chargeserver.repository.BasicRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Order(99)
@Repository("ErrorMsgRepositoryImpl")
public class ErrorMsgRepository extends BasicRepositoryImpl implements CommandLineRunner {

    private Map<String, String> errorInfoMap = new HashMap<>();

    @Autowired
    public ErrorMsgRepository(@Qualifier("MongoTemplate") MongoTemplate template) {
        super(template);
    }

    private void init() {
        List<ErrorCode> errorCodes = template.findAll(ErrorCode.class);
        for (ErrorCode ec: errorCodes) {
            errorInfoMap.put(ec.code, ec.msg);
        }
    }

    public String getMsg(String code) {
        String msg = errorInfoMap.get(code);
        return msg != null ? msg : "UNKNOWN";
    }

    @Override
    public void run(String... args) {
        init();
    }
}
