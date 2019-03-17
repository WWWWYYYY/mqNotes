package cn.enjoyedu.service.reg;


import cn.enjoyedu.vo.User;

/**
 * @author Mark老师   享学课堂 https://enjoy.ke.qq.com
 * <p>
 * 更多课程咨询 安生老师 QQ：669100976  VIP课程咨询 依娜老师  QQ：2470523467
 * <p>
 * 类说明：用户注册的接口，因为我们将有多个实现
 */
public interface IUserReg {

    public boolean userRegister(User user);

}
