package com.ahamo.common.exception;

public enum ErrorCode {
    VALIDATION_ERROR("入力内容に不備があります", ErrorSeverity.WARNING, "入力内容を確認して再度お試しください"),
    INVALID_FORMAT("フォーマットが正しくありません", ErrorSeverity.WARNING, "正しい形式で入力してください"),
    
    UNAUTHORIZED("認証が必要です", ErrorSeverity.WARNING, "ログインしてから再度お試しください"),
    FORBIDDEN("この操作を実行する権限がありません", ErrorSeverity.WARNING, "管理者にお問い合わせください"),
    
    INTERNAL_ERROR("内部サーバーエラーが発生しました", ErrorSeverity.CRITICAL, "しばらく時間をおいて再度お試しください"),
    SERVICE_UNAVAILABLE("サービスが一時的に利用できません", ErrorSeverity.CRITICAL, "しばらく時間をおいて再度お試しください"),
    
    NOT_FOUND("指定されたリソースが見つかりません", ErrorSeverity.WARNING, "URLを確認して再度お試しください"),
    CONFLICT("データの競合が発生しました", ErrorSeverity.WARNING, "ページをリロードして再度お試しください"),
    
    INVENTORY_NOT_AVAILABLE("在庫が不足しています", ErrorSeverity.WARNING, "別の商品をお選びください"),
    RESERVATION_EXPIRED("予約期限が切れました", ErrorSeverity.WARNING, "再度予約をお願いします"),
    RESERVATION_NOT_FOUND("予約が見つかりません", ErrorSeverity.WARNING, "予約番号を確認してください"),
    INSUFFICIENT_STOCK("在庫数が不足しています", ErrorSeverity.WARNING, "在庫数を確認してください");
    
    private final String defaultMessage;
    private final ErrorSeverity severity;
    private final String resolution;
    
    ErrorCode(String defaultMessage, ErrorSeverity severity, String resolution) {
        this.defaultMessage = defaultMessage;
        this.severity = severity;
        this.resolution = resolution;
    }
    
    public String getDefaultMessage() {
        return defaultMessage;
    }
    
    public ErrorSeverity getSeverity() {
        return severity;
    }
    
    public String getResolution() {
        return resolution;
    }
}
