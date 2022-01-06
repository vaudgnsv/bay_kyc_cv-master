package com.thaivan.bay.branch.model;

public class CheckJson {

    private Header header;
    private body body;
    private String MAC;

    public Header getHeaderReq() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public class Header {
        private String version;
        private String devMask;
        private String imei;
        private String timestamp;
        private String token;

        public String getVersion() {
            return version;
        }
        public void setVersion(String version) {
            this.version = version;
        }
        //---------------------------------------------------------------
        public String getDevMask() {
            return devMask;
            }
        public void setDevMask(String devMask) {
            this.devMask = devMask;
        }

        //---------------------------------------------------------------
        public String getImei() {
            return imei;
        }
        public void setImei(String imei) {
            this.imei = imei;
        }

        //---------------------------------------------------------------
        public String getTimestamp() {
            return timestamp;
        }
        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        //---------------------------------------------------------------
        public String getToken() {
            return token;
        }
        public void setToken(String token) {
            this.token = token;
        }

    }

    public body getBody_json() {
        return body;
    }

    public void setBody_json(body body_json) {
        this.body = body_json;
    }

    public class body {
        private String jsonVersion;

        public String getJsonVersion() {
            return jsonVersion;
        }
        public void setJsonVersion(String jsonVersion) {
            this.jsonVersion = jsonVersion;
        }
    }

    public void setMAC(String MAC) {
        this.MAC = MAC;
    }
    public String getMAC() {
        return MAC;
    }

}
