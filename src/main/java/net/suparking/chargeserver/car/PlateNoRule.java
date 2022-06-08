package net.suparking.chargeserver.car;

import net.suparking.chargeserver.exception.FieldValidator;
import net.suparking.chargeserver.exception.ParamNotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlateNoRule extends FieldValidator {
    @ParamNotNull
    public MatchType matchType;
    public Integer position;
    @ParamNotNull
    public String key;

    private static final Logger log = LoggerFactory.getLogger(PlateNoRule.class);

    @Override
    public boolean validate() {
        if (!super.validate()) {
            return false;
        }
        if (matchType.equals(MatchType.EXACT)) {
            if (position == null) {
                log.error("position cannot be null");
                return false;
            }
        }
        return true;
    }

    public int matchedIndex(String plateNo) {
        if (matchType.equals(MatchType.EXACT)) {
            return plateNo.startsWith(key, position - 1) ? 0 : -1;
        } else {
            return plateNo.indexOf(key);
        }
    }

    public boolean exactMatch() {
        return matchType.equals(MatchType.EXACT);
    }

    public boolean fuzzyMatch() {
        return matchType.equals(MatchType.FUZZY);
    }

    @Override
    public String toString() {
        return "PlateNoRule{" + "matchType=" + matchType + ", position=" + position + ", key='" + key + '\'' + '}';
    }
}
