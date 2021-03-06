package org.mengyun.tcctransaction.spring.recover;

import org.mengyun.tcctransaction.SystemException;
import org.mengyun.tcctransaction.recover.TransactionRecovery;
import org.mengyun.tcctransaction.support.TransactionConfigurator;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;

/**
 * Created by changming.xie on 6/2/16.
 */
public class RecoverScheduledJob {

    private TransactionRecovery transactionRecovery;

    private TransactionConfigurator transactionConfigurator;

    private Scheduler scheduler;

    public void init() {

        try {
            MethodInvokingJobDetailFactoryBean jobDetail = new MethodInvokingJobDetailFactoryBean();
            jobDetail.setTargetObject(transactionRecovery);
            jobDetail.setTargetMethod("startRecover");
            jobDetail.setName("transactionRecoveryJob");
            jobDetail.setConcurrent(false);
            jobDetail.afterPropertiesSet();
            
            // 替换原版本cronTrigger方法
            CronTriggerFactoryBean cronTriggerFactoryBean=new CronTriggerFactoryBean();
            cronTriggerFactoryBean.setBeanName("transactionRecoveryCronTrigger");
            cronTriggerFactoryBean.setCronExpression(transactionConfigurator.getRecoverConfig().getCronExpression());
            cronTriggerFactoryBean.afterPropertiesSet();
//            CronTriggerBean cronTrigger = new CronTriggerBean();
//            cronTrigger.setBeanName("transactionRecoveryCronTrigger");
//            cronTrigger.setCronExpression(transactionConfigurator.getRecoverConfig().getCronExpression());
//            cronTrigger.afterPropertiesSet();

            scheduler.scheduleJob((JobDetail) jobDetail.getObject(), cronTriggerFactoryBean.getObject());

            scheduler.start();

        } catch (Exception e) {
            throw new SystemException(e);
        }
    }

    public void setTransactionRecovery(TransactionRecovery transactionRecovery) {
        this.transactionRecovery = transactionRecovery;
    }

    public void setTransactionConfigurator(TransactionConfigurator transactionConfigurator) {
        this.transactionConfigurator = transactionConfigurator;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }
}
