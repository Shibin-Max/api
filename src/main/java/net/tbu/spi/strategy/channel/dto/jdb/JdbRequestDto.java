package net.tbu.spi.strategy.channel.dto.jdb;

import java.util.List;

public class JdbRequestDto {
    private Integer action;
    private Long ts;
    private String parent;
    private String startTime;
    private String endTime;
    private List<Integer> gTypes;
    private List<Integer> gType;
    private String starttime;
    private String endtime;
    // 通用构造方法（用于 FreegameRequestDto 风格）
    public JdbRequestDto(Integer action, Long ts, String parent, String startTime, String endTime, List<Integer> gTypes) {
        this.action = action;
        this.ts = ts;
        this.parent = parent;
        this.startTime = startTime;
        this.endTime = endTime;
        this.gTypes = gTypes;
    }
    // 兼容 JdbOrderDto 风格的构造方法（字段名不同）
    public JdbRequestDto(Integer action, Long ts, String parent, List<Integer> gType, String starttime, String endtime) {
        this.action = action;
        this.ts = ts;
        this.parent = parent;
        this.starttime = starttime;
        this.endtime = endtime;
        this.gType = gType;
    }

    public JdbRequestDto() {
    }

    @Override
    public String toString() {
        return "JdbRequestDto{" +
                "action=" + action +
                ", ts=" + ts +
                ", parent='" + parent + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", gTypes=" + gTypes +
                ", gType=" + gType +
                ", starttime='" + starttime + '\'' +
                ", endtime='" + endtime + '\'' +
                '}';
    }

    public Integer getAction() {
        return action;
    }

    public void setAction(Integer action) {
        this.action = action;
    }

    public Long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public List<Integer> getgTypes() {
        return gTypes;
    }

    public void setgTypes(List<Integer> gTypes) {
        this.gTypes = gTypes;
    }

    public List<Integer> getgType() {
        return gType;
    }

    public void setgType(List<Integer> gType) {
        this.gType = gType;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }
}

