package com.zc.redis;

import com.zc.redis.operations.Ops4Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


/**
 * @author wangy
 * @version 1.0
 * @date 2019-08-07 / 10:43
 */
@Repository
public class RedisClientTemplate {


    private final Ops4Value ops4Value;

    public RedisClientTemplate(Ops4Value ops4Value) {
        this.ops4Value = ops4Value;
    }

    public Ops4Value getOps4Value() {
        return this.ops4Value;
    }






}
