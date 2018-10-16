package com.futao.springmvcdemo.utils

import com.futao.springmvcdemo.model.entity.BaseEntity

/**
 * @author futao
 * Created on 2018/10/15.
 */
class PageResultUtils<T : BaseEntity> {
    var sql = ""
    fun createCriteria(tableName: String): PageResultUtils<T> {
        sql += "select * from ${tablePrefix}_$tableName where 1=1 "
        return this
    }

    fun eq(columnName: String, value: Any): PageResultUtils<T> {
        sql += " and $columnName = $value "
        return this
    }

    fun orderBy(columnName: String): PageResultUtils<T> {
        sql += " order by $columnName"
        return this
    }

    fun page(pageNum: Int, pageSize: Int): PageResultUtils<T> {
        sql += " limit ${pageSize * (pageNum - 1)},$pageSize"
        return this
    }
}