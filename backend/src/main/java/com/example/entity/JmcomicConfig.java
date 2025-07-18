package com.example.entity;


public class JmcomicConfig {
    public Client client;
    public static class Client{
        public String impl;
        public String[] domain;
        public Integer retry_times;
        public Postman postman;
        public static class Postman{
            public MetaData meta_data;
            public static class MetaData{
                public String proxies;
                public Cookies cookies;
                public static class Cookies{
                    public String AVS;
                }
            }
        }
    }

    public DirRule dir_rule;
    public static class DirRule{
        public String base_dir;
        public String rule;
    }

    public Download download;
    public static class Download{
        public Boolean cache;
        public Image image;
        public static class Image{
            public Boolean decode;
            public String suffix;
        }
        public Threading threading;
        public static class Threading{
            public Integer image;
            public Integer photo;
        }
    }
}
