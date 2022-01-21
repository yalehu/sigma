package com.zone.auth.application.service.command;

import com.google.common.base.Preconditions;
import com.zone.auth.application.service.command.cmd.RoleCreateCommand;
import com.zone.auth.application.service.command.cmd.RoleUpdateCommand;
import com.zone.auth.domain.agg.RoleAgg;
import com.zone.auth.domain.repository.RoleAggRepository;
import com.zone.auth.shared.enums.AccountTypeEnum;
import com.zone.commons.entity.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: jianyong.zhu
 * @Date: 2022/1/20 8:19 下午
 * @Description:
 */
@Slf4j
@Service
public class RoleCmdService {

  @Autowired
  private RoleAggRepository roleAggRepository;

  /**
   * 创建角色
   */
  public Long create(RoleCreateCommand createCommand, LoginUser loginUser) {

    // 0. 校验账号类型
    Preconditions.checkState(AccountTypeEnum.isAdmin(loginUser.getAccountType()), "非管理员不能更新资源点");

    // 1. 获取角色聚合根
    RoleAgg roleAgg = RoleAgg.create(createCommand, loginUser);

    // 2. 落地角色数据
    return roleAggRepository.save(roleAgg);
  }

  /**
   * 更新角色
   */
  public Long update(RoleUpdateCommand updateCommand, LoginUser loginUser) {

    // 0. 校验账号类型
    Preconditions.checkState(AccountTypeEnum.isAdmin(loginUser.getAccountType()), "非管理员不能更新资源点");

    // 1. 获取角色详情
    RoleAgg roleAgg = roleAggRepository.queryById(updateCommand.getId());
    Preconditions.checkNotNull(roleAgg, "角色不存在");

    // 2. 更新角色数据
    roleAgg.update(updateCommand, loginUser);

    // 3. 落地角色数据
    return roleAggRepository.update(roleAgg);
  }

  /**
   * 删除角色
   */
  public Long delete(Long roleId, LoginUser loginUser) {

    // 0. 校验账号类型
    Preconditions.checkState(AccountTypeEnum.isAdmin(loginUser.getAccountType()), "非管理员不能更新资源点");

    // 1. 删除角色数据
    return roleAggRepository.delete(roleId);
  }

}
