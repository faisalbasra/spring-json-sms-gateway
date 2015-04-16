package com.opteral.springsms.database;

import com.opteral.springsms.exceptions.GatewayException;
import com.opteral.springsms.model.ACK;
import com.opteral.springsms.model.SMS;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class SmsDaoHibernate extends AbstractHibernateDao implements SmsDao {
    @Override
    public void insert(SMS sms) throws GatewayException {
        currentSession().save(sms);
    }

    @Override
    public void update(SMS sms) throws GatewayException {
        SMS onBD = getSMS(sms.getId());
        onBD.setSubid(sms.getSubid());
        onBD.setMsisdn(sms.getMsisdn());
        onBD.setDatetimeScheduled(sms.getDatetimeScheduled());
        onBD.setText(sms.getText());
        onBD.setSender(sms.getSender());
    }

    @Override
    public void delete(SMS sms) {
        currentSession().delete(sms);
    }

    @Override
    public SMS getSMS(long id)  {
        return (SMS) currentSession().get(SMS.class, id);
    }

    @Override
    public void updateSMS_Status(ACK ack) throws GatewayException {

        SMS sms = getSMSfromIdSMSC(ack.getIdSMSC());
        sms.setSms_status(ack.getSms_status());
    }

    @Override
    public List<SMS> getSMSForSend(Date aFecha) throws GatewayException {
        String queryString = "SELECT s.* FROM sms s WHERE status < :status AND (datetime_scheduled <= NOW() OR datetime_scheduled is NULL) ";
        SQLQuery query = currentSession().createSQLQuery(queryString);
        query.addEntity(SMS.class).setParameter("status", SMS.SMS_Status.ONSMSC.getValue());
        return query.list();
    }

    @Override
    public SMS getSMSfromIdSMSC(String idSMSC) throws GatewayException {
        try {
            return (SMS) currentSession()
                    .createCriteria(SMS.class)
                    .add(Restrictions.eq("idSMSC", idSMSC))
                    .list().get(0);
        } catch (Exception e) {
            throw new GatewayException("No SMS with idSMSC: "+idSMSC);
        }
    }
}
