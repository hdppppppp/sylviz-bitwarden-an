package com.bitwarden.network.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

/**
 * 紧急访问查看密码库响应
 *
 * @property ciphers 被信任人的密码项目列表
 * @property collections 集合列表
 */
@Serializable
data class EmergencyAccessViewVaultResponseJson(
    @SerialName("ciphers")
    val ciphers: List<Cipher>,

    @SerialName("collections")
    val collections: List<Collection>? = null,
) {
    /**
     * 密码项目（简化版，用于紧急访问查看）
     */
    @Serializable
    data class Cipher(
        @SerialName("id")
        val id: String? = null,

        @SerialName("type")
        val type: Int,

        @SerialName("data")
        val data: CipherData,

        @SerialName("name")
        val name: String,

        @SerialName("notes")
        val notes: String? = null,

        @SerialName("login")
        val login: LoginData? = null,

        @SerialName("card")
        val card: CardData? = null,

        @SerialName("identity")
        val identity: IdentityData? = null,

        @SerialName("secureNote")
        val secureNote: SecureNoteData? = null,

        @SerialName("favorite")
        val favorite: Boolean? = null,

        @SerialName("reprompt")
        val reprompt: Int? = null,

        @SerialName("collectionIds")
        val collectionIds: List<String>? = null,

        @SerialName("deletedDate")
        @Contextual
        val deletedDate: Instant? = null,

        @SerialName("revisionDate")
        @Contextual
        val revisionDate: Instant? = null,

        @SerialName("folderId")
        val folderId: String? = null,
    )

    /**
     * 密码数据
     */
    @Serializable
    data class CipherData(
        @SerialName("fields")
        val fields: List<Field>? = null,

        @SerialName("attachments")
        val attachments: List<Attachment>? = null,
    )

    /**
     * 登录数据
     */
    @Serializable
    data class LoginData(
        @SerialName("username")
        val username: String? = null,

        @SerialName("password")
        val password: String? = null,

        @SerialName("uris")
        val uris: List<Uri>? = null,

        @SerialName("totp")
        val totp: String? = null,
    )

    /**
     * URI 数据
     */
    @Serializable
    data class Uri(
        @SerialName("uri")
        val uri: String,

        @SerialName("match")
        val match: Int? = null,
    )

    /**
     * 卡片数据
     */
    @Serializable
    data class CardData(
        @SerialName("cardholderName")
        val cardholderName: String? = null,

        @SerialName("brand")
        val brand: String? = null,

        @SerialName("number")
        val number: String? = null,

        @SerialName("expMonth")
        val expMonth: String? = null,

        @SerialName("expYear")
        val expYear: String? = null,

        @SerialName("code")
        val code: String? = null,
    )

    /**
     * 身份信息
     */
    @Serializable
    data class IdentityData(
        @SerialName("title")
        val title: String? = null,

        @SerialName("firstName")
        val firstName: String? = null,

        @SerialName("lastName")
        val lastName: String? = null,

        @SerialName("address1")
        val address1: String? = null,

        @SerialName("city")
        val city: String? = null,

        @SerialName("country")
        val country: String? = null,

        @SerialName("postalCode")
        val postalCode: String? = null,

        @SerialName("phone")
        val phone: String? = null,

        @SerialName("email")
        val email: String? = null,
    )

    /**
     * 安全笔记数据
     */
    @Serializable
    data class SecureNoteData(
        @SerialName("type")
        val type: Int? = null,
    )

    /**
     * 字段数据
     */
    @Serializable
    data class Field(
        @SerialName("name")
        val name: String,

        @SerialName("value")
        val value: String? = null,

        @SerialName("type")
        val type: Int,
    )

    /**
     * 附件数据
     */
    @Serializable
    data class Attachment(
        @SerialName("id")
        val id: String,

        @SerialName("url")
        val url: String? = null,

        @SerialName("fileName")
        val fileName: String,

        @SerialName("key")
        val key: String? = null,

        @SerialName("size")
        val size: String? = null,

        @SerialName("sizeName")
        val sizeName: String? = null,
    )

    /**
     * 集合数据
     */
    @Serializable
    data class Collection(
        @SerialName("id")
        val id: String,

        @SerialName("organizationId")
        val organizationId: String,

        @SerialName("name")
        val name: String,

        @SerialName("externalId")
        val externalId: String? = null,
    )
}

/**
 * 紧急访问接管账户响应
 * 包含接管账户所需的密钥信息
 *
 * @property keyEncrypted 被加密的主密钥
 * @property privateKey 被加密的私钥
 * @property collectionIds 集合ID列表
 */
@Serializable
data class EmergencyAccessTakeoverResponseJson(
    @SerialName("keyEncrypted")
    val keyEncrypted: String,

    @SerialName("privateKey")
    val privateKey: String,

    @SerialName("collectionIds")
    val collectionIds: List<String>? = null,
)

/**
 * 密码重置请求（接管后设置新密码）
 *
 * @property newMasterPasswordHash 新主密码哈希
 * @property key 被加密的密钥
 */
@Serializable
data class EmergencyAccessPasswordRequestJson(
    @SerialName("newMasterPasswordHash")
    val newMasterPasswordHash: String,

    @SerialName("key")
    val key: String,
)
