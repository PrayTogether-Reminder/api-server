package site.praytogether.pray_together.exception;

import java.util.HashMap;
import java.util.Map;

public class ExceptionField {
    private final Map<String,Object> fields;

    private ExceptionField(Map<String,Object> fields){
        this.fields = fields;
    }

    public Map<String,Object> get() {
        return fields;
    }

    public static Builder builder(){
        return new Builder();
    }

    private static class Builder{
        private final Map<String,Object> fields;
        private Builder(){
            this.fields = new HashMap<>();
        }

        public Builder add(String key, Object value){
            this.fields.put(key, value);
            return this;
        }

        public ExceptionField build(){
            return new ExceptionField(fields);
        }
    }
}