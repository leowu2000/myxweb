package com.mmstart.core;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class CommonDAO extends HibernateDaoSupport {
	/**
     *将实体类的信息存入数据库中相应的表
     *@param entity 实体类的对象
     */
	public void save(Object entity) {
		//调用基类的getHibernateTemplate方法获得一个HibernateTemplate,再运行此模版的save方法向数据库中新增记录
		getHibernateTemplate().save(entity);
	}

	/**
     *在数据库中删除实体类相对应的表中的数据
     *@param entity 实体类的对象
     */
	public void delete(Object entity) {
		//调用基类的getHibernateTemplate方法获得一个HibernateTemplate,再运行此模版的delete方法从数据库中删除相应记录
		getHibernateTemplate().delete(entity);
	}

	/**
     *修改数据库中相应的数据
     *@param entity 实体类的对象
     *@return Object entity
     */
	public Object merge(Object entity) {
		//调用基类的getHibernateTemplate方法获得一个HibernateTemplate,再运行此模版的merge方法修改数据库中相应数据,返回修改后的entity
		return getHibernateTemplate().merge(entity);
	}

	/**
     *根据id和类定义查找数据库中对应表中的对应记录
     *@param entityClass 实体类的类定义
     *@param id ID
     *@return Object entity
     */
	public Object findById(Class<?> entityClass, String id) {
		//调用基类的getHibernateTemplate方法获得一个HibernateTemplate,再运行此模版的get方法获取对应的数据,实例化entityClass并返回
		return getHibernateTemplate().get(entityClass, id);
	}

	/**
     *根据类定义查找出数据库中对应表的所有记录
     *@param entityClass 实体类的类定义
     *@return List<entityClass> list
     */
	public List<?> findAll(final Class<?> entityClass) {
		//调用基类的getHibernateTemplate方法获得一个HibernateTemplate,再运行此模版的excute方法来执行特定的操作，返回一个封装好的list，list中每一条都是一个entityClass对象
		return (List<?>) getHibernateTemplate().execute(new HibernateCallback() {
			//实例化HibernateCallback,实现doInHibernate方法
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				//实例化Criteria,初始化结果集为entityClass的对象集合,以list封装返回(相当于"select * from " + entitiClass得到的结果集封装成list)
				return session.createCriteria(entityClass).list();
			}
		});
	}

	/**
     *按页码根据类定义查找出数据库中对应表的所有记录
     *@param entityClass 实体类的类定义
     *@param page 页码
     *@return PageList 带分页信息的list
     */
	public PageList findAll(final Class<?> entityClass, final int page) {
		//调用基类的getHibernateTemplate方法获得一个HibernateTemplate,再运行此模版的excute方法来执行特定的操作，返回一个封装好的PageList，包含当前的分页信息和当前页所有数据组成的list
		return (PageList) getHibernateTemplate().execute(new HibernateCallback() {
			//实例化HibernateCallback,实现doInHibernate方法
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				//实例化Criteria对象c,初始化内容为entityClass的对象集合
				Criteria c = session.createCriteria(entityClass);
				//实例化PageInfo对象pi,初始化分页信息,page为当前页码,c.list().size()为数据库中所有记录的总数
				PageInfo pi = new PageInfo(page, c.list().size());
				//设定结果集的起点、范围,firstresult是设定结果集起始位置,maxresults是设定结果集最大记录数
				c.setFirstResult(PageInfo.PAGESIZE * (page - 1)).setMaxResults(PageInfo.PAGESIZE);
				//将pageinfo和结果集的list形式进行封装并返回
				return new PageList(pi, c.list());
			}
		});
	}

	/**
     *按页码、查询条件、排序信息和类定义查找出数据库中对应表的所有记录
     *@param entityClass 实体类的类定义
     *@param page 页码
     *@param params 封装查询条件的list
     *@param page 封装排序信息的list
     *@return PageList 带分页信息的list
     */
	public PageList findAll(final Class<?> entityClass, final int page, final List<Param> params, final List<Ord> ords) {
		//调用基类的getHibernateTemplate方法获得一个HibernateTemplate,再运行此模版的excute方法来执行特定的操作，返回一个封装好的PageList，包含当前的分页信息和当前页所有数据组成的list
		return (PageList) getHibernateTemplate().execute(new HibernateCallback() {
			//实例化HibernateCallback,实现doInHibernate方法
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				//实例化Criteria对象c,初始化内容为entityClass的对象集合
				Criteria c = session.createCriteria(entityClass);
				//循环将所有查询条件加在结果集上
				for (Param param : params) {
					//为结果集添加条件查询
					c.add(Restrictions.like(param.getCol(), param.getValue()));
				}
				//实例化PageInfo对象pi,初始化分页信息,page为当前页码,后面参数为当前结果集中数据条数
				PageInfo pi = new PageInfo(page, (Integer)c.setProjection(Projections.rowCount()).uniqueResult());
				//应用投影到一个查询
				c.setProjection(null);
				//循环将排序信息加在结果集上
				for (Ord ord : ords) {
					//判断是否升序排列
					if (ord.getAscending())
						//按列名col升序排列
						c.addOrder(Order.asc(ord.getCol()));
					else
						//按列名col降序排列
						c.addOrder(Order.desc(ord.getCol()));
				}
				//设定结果集的起点、范围,firstresult是设定结果集起始位置,maxresults是设定结果集最大记录数
				c.setFirstResult(PageInfo.PAGESIZE * (page - 1)).setMaxResults(PageInfo.PAGESIZE);
				//将pageinfo和结果集的list形式进行封装并返回
				return new PageList(pi, c.list());
			}
		});
	}

	/**
     *执行用户编写的hql脚本
     *@param hql hibernate方言编写的脚本
     *@param page 页码
     *@return PageList 带分页信息的list
     */
	public PageList findByHQL(final String hql, final int page) {
		//实例化PageInfo对象pi,初始化分页信息,page为当前页码,后面参数为hql执行后查询的数据条数
		PageInfo pi = new PageInfo(page, getHibernateTemplate().find(hql).size());
		//调用基类的getHibernateTemplate方法获得一个HibernateTemplate,再运行此模版的excuteFind方法来执行特定的操作，返回一个封装好的list，list中每一条都是一个表名对应的对象
		List<?> list = getHibernateTemplate().executeFind(new HibernateCallback() {
			//实例化HibernateCallback,实现doInHibernate方法
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				//执行hql脚本,获得查询结果集
				Query query = session.createQuery(hql);
				//设定结果集的起点、范围,firstresult是设定结果集起始位置,maxresults是设定结果集最大记录数
				return query.setFirstResult(PageInfo.PAGESIZE * (page - 1)).setMaxResults(PageInfo.PAGESIZE).list();
			}
		});
		//将pageinfo和list进行封装并返回
		return new PageList(pi, list);
	}

	/**
     *执行用户编写的hql脚本
     *@param hql hibernate方言编写的脚本
     *@return List<?> list
     */
	public List<?> findByHQL(final String hql) {
		//调用基类的getHibernateTemplate方法获得一个HibernateTemplate,再运行此模版的find方法执行hql脚本,返回一个list
		return getHibernateTemplate().find(hql);
	}
}