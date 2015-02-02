package net.d80harri.wr.service;

import net.d80harri.wr.db.SessionHandler;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class TransactionAspect {
	private static final Logger logger = LoggerFactory.getLogger(TransactionAspect.class);
	public static boolean isActive = true;
	
	private static int depth = 0;
	private static Transaction tx;
	
	@Pointcut("@annotation(net.d80harri.wr.service.Transactional)")
    void transactional() {}
	
	@Around("transactional()")
	public Object handleTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
		if (isActive) {
			return handleWithinTx(joinPoint);
		} else {
			return joinPoint.proceed();
		}
	}

	private Object handleWithinTx(ProceedingJoinPoint joinPoint)
			throws Throwable {
		Object result = null;
		Session session = SessionHandler.getInstance().getSession();
		
		synchronized (TransactionAspect.class) {
			if (tx == null) {
				logger.info("Starting transaction for " + joinPoint.getSignature().getName());
				tx = session.beginTransaction();
				depth = 1;
			} else {
				depth = depth + 1;
			}
		}

		
		try {
			result = joinPoint.proceed();
			
			synchronized (TransactionAspect.class) {
				depth--;
				if (depth == 0){
					tx.commit();
					logger.info("Committed transaction for " + joinPoint.getSignature().getName());
					tx = null;
				}
			}
		} catch (Throwable t) {
			synchronized (TransactionAspect.class) {
				depth--;
				if (depth == 0) {
					tx.rollback();
					logger.info("Rolled back transaction for " + joinPoint.getSignature().getName());
					tx = null;
				}
			}
			throw t;
		} finally {
			synchronized (TransactionAspect.class) {
				if (depth == 0) {
					session.close();
				}			
			}
		}
		
		return result;
	}
}
