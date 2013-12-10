/** 
*Copyright 2013 Software Mind SA
*
*Licensed under the Apache License, Version 2.0 (the "License");
*you may not use this file except in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing, software
*distributed under the License is distributed on an "AS IS" BASIS,
*WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*See the License for the specific language governing permissions and
*limitations under the License.
*/
package pl.swmind.robust.sioc.utils;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import pl.swmind.robust.sioc.core.DomainObject;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.Date;
import java.util.List;

/**
 * TODO: Type description here! <br>
 * <p/>
 * Creation date: 05/09/12<br>
 *
 * @author dalo (<a href="http://www.softwaremind.pl">SoftwareMind</a>)
 */
public abstract class CriteriaDao<T extends DomainObject> implements GenericDao<T>{
    private Logger log = Logger.getLogger(CriteriaDao.class);
    private EntityManagerFactory entityManagerFactory;
    private Class<T> clazz;

    protected CriteriaDao(Class<T> clazz) {
        this.clazz = clazz;
    }

    public List<T> findByCreatedBetween(Date start, Date end) {
        return getCriteriaFor(clazz)
            .add(Restrictions.between("created",start,end))
            .list();
    }

    @Value("${uri.name}")
    private String uriName;

    public String getUriName() {
        return uriName;
    }

    @Autowired
    public void setEntityManagerFactory(EntityManagerFactory emf) {
        this.entityManagerFactory = emf;
    }

    public void delete(T entity){
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(Iterable<? extends T> entities) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public T findByUri(String uri) {
        Criterion restriction = getUriRestriction("",uri);
        return (T) getCriteriaFor(clazz)
            .add(restriction)
            .uniqueResult();
    }

    @Override
    public List<T> findAll() {
        return getCriteriaFor(clazz)
            .list();
    }

    @Override
    public long count() {
        return (Long) getCriteriaFor(clazz)
            .setProjection(Projections.rowCount())
            .uniqueResult();
    }

    @Override
    public void delete(String uri) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<T> findAll(Sort sort) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T save(T entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<T> save(Iterable<? extends T> entities) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T findOne(String uri) {
        return findByUri(uri);
    }

    @Override
    public boolean exists(String uri) {
        return findByUri(uri) != null;
    }

    @Override
    public void flush() {
        throw new UnsupportedOperationException();
    }

    @Override
    public T saveAndFlush(T entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteInBatch(Iterable<T> entities) {
        throw new UnsupportedOperationException();
    }

    protected Criteria getCriteriaFor(Class<T> clazz){
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Session session = (Session) entityManager.getDelegate();
        return session.createCriteria(clazz);
    }

    protected Criterion getUriRestriction(String entity, String uri){
        Criterion restriction = Restrictions.eq(entity + uriName, uri);
        if(uriName.equals("uriFromBigInt")){
            log.info("Quering " + entity + " by Long uri");
            restriction = Restrictions.eq(entity + uriName,Long.parseLong(uri));
        }else{
            log.info("Quering " + entity + " by String uri");
        }
        return restriction;
    }
}
