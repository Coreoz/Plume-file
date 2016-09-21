/**
 * Enable to use HibernateIdGenerator on identifiers in the package: <code>@GeneratedValue(generator = HibernateIdGenerator.NAME)</code>
 */
@org.hibernate.annotations.GenericGenerator(name = com.coreoz.plume.db.hibernate.utils.HibernateIdGenerator.NAME, strategy = "com.coreoz.plume.db.hibernate.utils.HibernateIdGenerator")
package com.coreoz.plume.file.db.entities;