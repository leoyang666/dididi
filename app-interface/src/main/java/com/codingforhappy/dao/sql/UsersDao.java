package com.codingforhappy.dao.sql;

import com.codingforhappy.model.CheckableUser;
import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface UsersDao {

    boolean hasExisted(@Param("phonenum") String phoneNum, @Param("table") String table);

    void addUser(@Param("user") CheckableUser user, @Param("table") String table);
}
