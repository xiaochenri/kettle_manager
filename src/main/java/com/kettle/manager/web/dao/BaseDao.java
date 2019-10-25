package com.kettle.manager.web.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * @author: luoxu
 * @date: 2019-05-06 10:33
 */
@NoRepositoryBean
public interface BaseDao<T, ID extends Serializable> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    /**
     * 执行SQL查询语句来查询单条记录。
     *
     * @param sql
     *            SQL语句
     * @param param
     *            查询参数值
     *
     * @return 查询到的结果
     */
    Map<String, Object> findForObject(String sql, Object... param);

    /**
     * 执行SQL查询语句来查询多条记录。
     *
     * @param sql
     *            SQL语句
     * @param param
     *            查询参数值
     *
     * @return 查询到的结果
     */
    List<Map<String, Object>> findForList(String sql, Object... param);

    /**
     * 执行SQL查询语句来查询多条记录。
     *
     * @param sql
     *            SQL语句
     * @param param
     *            查询参数值
     *
     * @return 查询到的结果
     */
    List<T> findAll(String sql, Object... param);

    /**
     * 执行SQL语句来统计记录条数。
     *
     * @param sql
     *            SQL语句
     * @param param
     *            查询参数值
     *
     * @return 满足条件的记录总数
     */
    long count(String sql, Object... param);

    /**
     * 执行SQL语句来修改（更新或删除）记录。
     *
     * @param sql
     *            SQL语句
     * @param param
     *            查询参数值
     *
     * @return 成功修改记录的条数。
     *
     * @return 受影响的记录数
     */
    int update(String sql, Object... param);

}
