package com.x8bit.bitwarden.data.organization.di

import com.x8bit.bitwarden.data.organization.repository.OrganizationRepository
import com.x8bit.bitwarden.data.organization.repository.OrganizationRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 组织管理模块
 * 提供 OrganizationRepository 的依赖注入绑定
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class OrganizationModule {

    @Binds
    @Singleton
    abstract fun bindOrganizationRepository(
        impl: OrganizationRepositoryImpl,
    ): OrganizationRepository
}
