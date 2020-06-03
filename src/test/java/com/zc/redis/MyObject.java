package com.zc.redis;

/**
 * @author wangy
 * @version 1.0
 * @date 2019-08-07 / 14:42
 */
public class MyObject {
    private int id;
    private String name;
    private String character;

    public MyObject() {
    }

    MyObject(int id, String name, String character) {
        this.id = id;
        this.name = name;
        this.character = character;
    }

    @Override
    public String toString() {
        return "MyObject{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", character='" + character + '\'' +
            '}';
    }
}
