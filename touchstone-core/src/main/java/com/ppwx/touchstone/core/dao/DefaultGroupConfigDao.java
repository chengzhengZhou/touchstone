/**
 * Copyright (C), 2010-2023, 爱回收
 * FileName: DefaultGroupConfigDao
 * Author:   Mikey(ext.ahs.zhouchzh1 @ jd.com)
 * Date:     2023/10/30 17:36
 * Description:
 */
package com.ppwx.touchstone.core.dao;

import com.ppwx.touchstone.core.domain.GroupConfig;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * 试金石元数据增删改实现
 *
 * @author Mikey(ext.ahs.zhouchzh1 @ jd.com)
 * @date 2023/10/30 17:36
 * @since 1.0.0
 */
public class DefaultGroupConfigDao implements GroupConfigDao {
    /**
     * 库名
     */
    private String schema;
    /**
     * 表名
     */
    private String tableName = "t_touchstone_group";

    public DefaultGroupConfigDao() {
    }

    /**
     * 构造函数用于初始化DefaultGroupConfigDao对象
     * @param schema 数据库模式名称
     * @param tableName 数据表名称
     */
    public DefaultGroupConfigDao(String schema, String tableName) {
        this.schema = schema;
        this.tableName = tableName;
    }

    /**
     * {@link JdbcTemplate}
     */
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(GroupConfig config) {
        config.normalizeGroup();
        if (config.getId() == null) {
            // insert
            String namespace = config.getNamespace();
            String sql = "INSERT INTO " + getTable() + "(`namespace`,`test_name`,`test_groups`,`rnd_seed`) VALUES(?,?,?,?)";
            jdbcTemplate.update(sql, namespace, config.getTestName(), config.getTestGroups(), config.getRndSeed());
        } else {
            // update test_groups
            String sql = "UPDATE " + getTable() + " SET `test_groups`=?,`whitelist`=? WHERE id=?";
            jdbcTemplate.update(sql, config.getTestGroups(), config.getWhitelist(), config.getId());
        }
    }

    @Override
    public int delete(GroupConfig config) {
        if (config == null || config.getId() == null) {
            return 0;
        }
        String sql = "DELETE FROM " + getTable() + " WHERE id=?";
        return jdbcTemplate.update(sql, config.getId());
    }

    @Override
    public GroupConfig getGroupConfig(String namespace, String testName) {
        String sql = "SELECT " +
                "`id`," +
                "`namespace`," +
                "`test_name` testName," +
                "`test_groups` testGroups," +
                "`whitelist` whitelist," +
                "`rnd_seed` rndSeed," +
                "`update_dt` updateDt " +
                "FROM " + getTable() + " where `namespace`=? and test_name=? ORDER BY `id` DESC LIMIT 1";
        List<GroupConfig> list = jdbcTemplate.query(sql, new Object[]{namespace, testName}, new BeanPropertyRowMapper<>(GroupConfig.class));
        return list.isEmpty() ? null : list.iterator().next();
    }

    @Override
    public List<GroupConfig> listAllByNamespace(String namespace) {
        String sql = "SELECT " +
                "`id`," +
                "`namespace`," +
                "`test_name` testName," +
                "`test_groups` testGroups," +
                "`whitelist` whitelist," +
                "`rnd_seed` rndSeed," +
                "`update_dt` updateDt " +
                "FROM " + getTable() + " where `namespace`=? LIMIT 500";
        return jdbcTemplate.query(sql, new Object[]{namespace}, new BeanPropertyRowMapper<>(GroupConfig.class));
    }

    @Override
    public Long getVersionByNamespace(String namespace) {
        String sql = "SELECT MAX(update_dt) FROM " + getTable() + " WHERE namespace=?";
        try{
            Date date = jdbcTemplate.queryForObject(sql, new Object[]{namespace}, new SingleColumnRowMapper<>(Date.class));
            return date == null ? null : date.getTime();
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<String> listAllNamespace() {
        String sql = "SELECT DISTINCT(namespace) FROM " + getTable() + " LIMIT 500";
        return jdbcTemplate.queryForList(sql, null, String.class);
    }

    private String getTable() {
        if (StringUtils.isEmpty(schema)) {
            return tableName;
        }
        return schema + "." + tableName;
    }
}