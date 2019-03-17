package cn.enjoyedu.aop;

import cn.enjoyedu.vo.User;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Service;

/**
 * @author Mark老师   享学课堂 https://enjoy.ke.qq.com
 * <p>
 * 更多课程咨询 安生老师 QQ：669100976  VIP课程咨询 依娜老师  QQ：2470523467
 * <p>
 * 类说明：统计方法执行时间的Aop实现
 */
@Aspect
@Service
public class StatTime {

    private User user;

    public StatTime() {
        System.out.println("************Aop开启");
    }

    @Pointcut("execution(* cn.enjoyedu.service.reg.impl.*.*Register(..))")
    public void stat() {
    }

    @Around("stat()&&args(user)")
    public Object statTime(ProceedingJoinPoint proceedingJoinPoint, User user) {
        this.user = user;
        long start = System.currentTimeMillis();
        Object result = null;
        try {
            result = proceedingJoinPoint.proceed(new Object[]{this.user});
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        System.out.println("************spend time : " + (System.currentTimeMillis() - start) + "ms");
        return result;

    }

}
