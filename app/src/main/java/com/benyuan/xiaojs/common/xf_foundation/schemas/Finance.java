package com.benyuan.xiaojs.common.xf_foundation.schemas;

/**
 * Created by maxiaobao on 2016/11/4.
 */

public class Finance {

    //
    // Defines the pricing types.
    //
    public class PricingType {

        // 总价
        public static final int TOTAL = 1;
        // 实际课时费
        public static final int PAY_PER_HOUR = 2;
        // 累计
        public static final int ACCUMULATED = 3;

    }

    //
    // Defines the purchase types.
    //
    public class PurchaseType {
        // 新订
        public static final int NEW = 1;
        // 续订
        public static final int RENEWAL = 2;
        // 升级
        public static final int UPGRADE = 3;
        // 后台赠送
        public static final int PRESENT = 4;
        // 后台自动续订
        public static final int AUTORENEWAL = 5;

    }

    //endregion

    //
    // Defines the coffers states.
    //
    public class CofferStatus {
        public static final String ACTIVE = "Active";
        // No withdraw
        public static final String FROZEN = "Frozen";
        public static final String INACTIVE = "Inactive";
    }


    //
    // Defines the transaction states.
    //
    public class PaymentStatus {

        // Not started
        public static final int NA = 0;
        // In progressing
        public static final int PROCESSING = 1;
        public static final int SUCCESS = 2;
        public static final int FAILURE = 3;
    }

    //
    // Defines the finance review or reconciliation status.
    //
    public class ReviewStatus {
        public static final int NA = 0;
        // Reviewed by reconciliation accountant
        public static final int REVIEWED = 1;
        // Confirmed by accountant manager
        public static final int CONFIRMED = 2;
    }

    //
    // Defines the transaction peer types.
    //
    public class PeerType {
        // Non-mixed
        public static final int COFFER = 1;
        public static final int COFFER_BONUS = 2;
        // Reserved for internal use only
        public static final int COFFER_FROZEN = 3;
        // Settlement transaction as commit peer side
        public static final int SETTLEMENT_TRANSACTION = 10;
        // ATM or other kind of bank transferring
        public static final int BANK = 20;
        // Depicts a payment gateway-defined channel, e.g. ICBC-DEBI
        public static final int GATEWAY_CHANNEL = 21;
        // Depicts a gateway account, e.g. account @ alipay
        public static final int GATEWAY_ACCOUNT = 22;
    }


    //
    // Defines the payment gateways supported by Kingschina.cn.
    //
    public class PaymentGateway {
        public static final int ALI_PAY = 1;
    }

    //
    // Defines the pay methods.
    //
    public class CurrencyType {
        public static final int GCOIN = 1;
        public static final int RMB = 2;
    }

    //
    // Defines the payment gateway-defined channel types that are supported by Kingschina.cn.
    //
    public class GatewayChannelType {
        // Alipay-defined mixed channel
        public static final int ALIPAY_MIXED = 1;
        // Alipay-defined debit-only channel
        public static final int ALIPAY_DEBIT_ONLY = 2;
        // 支付平台
        public static final int PLATFORM = 3;
    }

    //
    // Defines the bank account types.
    //
    public class BankAccountType {
        // business account
        public static final int BUSINESS = 1;
        // personal account
        public static final int PERSONAL = 2;
    }

    //
    // Defines the bank card types.
    //
    public class BankCardType {
        public static final int CREDIT = 1;
        public static final int DEBIT = 2;

    }

}
