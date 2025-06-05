# API命名規則ガイドライン

**作成日**: 2024年12月  
**対象**: SCRUM-77 - API仕様統一設計  
**バージョン**: 1.0

---

## 1. 概要

本ドキュメントは、オンライン契約システムのAPI設計における命名規則を定義し、フロントエンドとバックエンド間の一貫性を確保することを目的としています。

### 1.1 適用範囲
- REST API エンドポイント
- JSON フィールド名
- HTTP ヘッダー
- クエリパラメータ
- レスポンス構造

---

## 2. 基本原則

### 2.1 一般原則
- **一貫性**: 同じ概念には同じ命名を使用
- **予測可能性**: 規則に従えば命名が予測できる
- **可読性**: 英語圏でない開発者にも理解しやすい
- **簡潔性**: 必要十分な情報を含む最短の命名

### 2.2 言語
- **API**: 英語を使用
- **ドキュメント**: 日本語と英語併記
- **エラーメッセージ**: 日本語（国際化対応時に多言語化）

---

## 3. エンドポイント命名規則

### 3.1 URL構造
```
/api/v1/{resource}/{id}/{sub-resource}/{sub-id}
```

### 3.2 リソース名
- **複数形**を使用（RESTful原則）
- **小文字** + **ハイフン区切り**
- **名詞**を使用（動詞は避ける）

#### ✅ 良い例
```
GET /api/v1/plans
GET /api/v1/devices  
GET /api/v1/contracts
GET /api/v1/shipping-addresses
GET /api/v1/payment-methods
```

#### ❌ 悪い例
```
GET /api/v1/plan           # 単数形
GET /api/v1/getPlans       # 動詞を含む
GET /api/v1/paymentMethods # camelCase
```

### 3.3 HTTPメソッドとエンドポイント対応

| 操作 | メソッド | エンドポイント例 | 説明 |
|------|----------|------------------|------|
| 一覧取得 | GET | `/plans` | プラン一覧 |
| 詳細取得 | GET | `/plans/{id}` | 特定プラン詳細 |
| 作成 | POST | `/contracts` | 新規契約作成 |
| 更新 | PUT | `/contracts/{id}` | 契約全体更新 |
| 部分更新 | PATCH | `/contracts/{id}` | 契約部分更新 |
| 削除 | DELETE | `/contracts/{id}` | 契約削除 |

### 3.4 サブリソース
```
GET /contracts/{id}/payments      # 契約の決済情報
GET /devices/{id}/inventory       # 機種の在庫情報
POST /contracts/{id}/submit       # 契約確定アクション
```

### 3.5 アクションエンドポイント
非CRUD操作の場合、動詞を含むアクションエンドポイントを使用

```
POST /auth/login              # ログイン
POST /auth/refresh            # トークンリフレッシュ
POST /contracts/{id}/submit   # 契約確定
POST /mnp/eligibility         # MNP利用可能性確認
```

---

## 4. JSONフィールド命名規則

### 4.1 基本ルール
- **snake_case**を使用
- **小文字**のみ
- **名詞**を基本とする
- **略語**は避ける（必要な場合は一般的なもののみ）

#### ✅ 良い例
```json
{
  "user_id": "uuid",
  "email_address": "user@example.com",
  "phone_number": "090-1234-5678",
  "created_at": "2024-12-20T10:30:00Z",
  "is_active": true,
  "shipping_address": {
    "postal_code": "100-0001",
    "prefecture": "東京都"
  }
}
```

#### ❌ 悪い例
```json
{
  "userId": "uuid",           # camelCase
  "email": "user@example.com", # 不完全な名前
  "tel": "090-1234-5678",     # 略語
  "createdAt": "2024-12-20",  # camelCase
  "active": true,             # is_ prefix なし
  "addr": {                   # 略語
    "zip": "100-0001"         # 略語
  }
}
```

### 4.2 特殊なフィールド

#### 4.2.1 Boolean値
`is_`, `has_`, `can_`, `should_` などのプレフィックスを使用

```json
{
  "is_active": true,
  "has_shipping_address": false,
  "can_edit": true,
  "should_verify": false
}
```

#### 4.2.2 日時
ISO 8601形式を使用し、`_at`または`_date`サフィックス

```json
{
  "created_at": "2024-12-20T10:30:00Z",
  "updated_at": "2024-12-20T10:30:00Z", 
  "birth_date": "1990-01-01",
  "delivery_date": "2024-12-25"
}
```

#### 4.2.3 ID・参照
`_id`サフィックスを使用

```json
{
  "user_id": "uuid",
  "plan_id": "plan_basic_001",
  "device_id": "device_iphone15_001",
  "contract_id": "uuid"
}
```

#### 4.2.4 金額
通貨単位を明確にし、小数点以下まで対応

```json
{
  "monthly_fee": 2980.00,
  "device_price": 124800.00,
  "total_amount": 127780.00,
  "tax_amount": 12778.00
}
```

---

## 5. クエリパラメータ命名規則

### 5.1 基本ルール
- **snake_case**を使用
- **小文字**のみ
- **省略形**は最小限に

### 5.2 標準パラメータ

#### ページネーション
```
?page=1&limit=20
```

#### フィルタリング
```
?category=iPhone
?price_range=premium
?in_stock=true
?status=active
```

#### ソート
```
?sort_by=created_at
?sort_order=desc
?sort=name:asc,price:desc
```

#### 検索
```
?q=iPhone+15        # 一般的な検索
?search=iPhone      # 明示的な検索
```

#### フィールド選択
```
?fields=id,name,price
?include=reviews,specifications
?exclude=internal_notes
```

---

## 6. HTTPヘッダー命名規則

### 6.1 標準ヘッダー
```
Content-Type: application/json
Authorization: Bearer {token}
Accept: application/json
```

### 6.2 カスタムヘッダー
`X-`プレフィックスを使用

```
X-Request-ID: uuid
X-API-Version: v1
X-Client-Platform: web
X-User-Agent: online-contract-app/1.0
```

---

## 7. エラーコード命名規則

### 7.1 エラーコード形式
大文字 + アンダースコア区切り

```json
{
  "error_code": "VALIDATION_ERROR",
  "message": "入力内容に不備があります"
}
```

### 7.2 標準エラーコード

#### 認証・認可関連
```
AUTHENTICATION_ERROR    # 認証失敗
AUTHORIZATION_ERROR     # 認可失敗
TOKEN_EXPIRED          # トークン期限切れ
INVALID_CREDENTIALS    # 不正な認証情報
```

#### バリデーション関連
```
VALIDATION_ERROR       # 一般的なバリデーションエラー
REQUIRED_FIELD_MISSING # 必須フィールド不足
INVALID_FORMAT         # 不正なフォーマット
VALUE_OUT_OF_RANGE     # 値が範囲外
```

#### リソース関連
```
RESOURCE_NOT_FOUND     # リソースが見つからない
RESOURCE_CONFLICT      # リソースの競合
RESOURCE_EXPIRED       # リソースの期限切れ
INSUFFICIENT_INVENTORY # 在庫不足
```

#### 外部サービス関連
```
EXTERNAL_SERVICE_ERROR    # 外部サービスエラー
PAYMENT_GATEWAY_ERROR     # 決済ゲートウェイエラー
EKYC_SERVICE_UNAVAILABLE  # eKYCサービス利用不可
MNP_SERVICE_ERROR         # MNPサービスエラー
```

---

## 8. データ型とフォーマット標準

### 8.1 日時
- **ISO 8601形式**を使用
- **UTC**を基本とし、タイムゾーン情報を含む

```json
{
  "created_at": "2024-12-20T10:30:00Z",
  "updated_at": "2024-12-20T19:30:00+09:00"
}
```

### 8.2 通貨
- **数値型**を使用（文字列ではない）
- **小数点以下2桁**まで対応

```json
{
  "price": 2980.00,
  "tax": 298.00
}
```

### 8.3 電話番号
日本の電話番号形式に統一

```json
{
  "phone_number": "090-1234-5678",
  "landline": "03-1234-5678"
}
```

### 8.4 郵便番号
ハイフン区切りの7桁形式

```json
{
  "postal_code": "100-0001"
}
```

---

## 9. レスポンス構造標準

### 9.1 成功レスポンス

#### 単一リソース
```json
{
  "id": "uuid",
  "name": "リソース名",
  "created_at": "2024-12-20T10:30:00Z"
}
```

#### リスト
```json
{
  "data": [
    {
      "id": "uuid",
      "name": "リソース1"
    }
  ],
  "pagination": {
    "page": 1,
    "limit": 20,
    "total": 100,
    "total_pages": 5,
    "has_next": true,
    "has_prev": false
  }
}
```

### 9.2 エラーレスポンス
```json
{
  "error_code": "VALIDATION_ERROR",
  "message": "入力内容に不備があります",
  "details": [
    {
      "field": "email",
      "code": "INVALID_FORMAT",
      "message": "有効なメールアドレスを入力してください"
    }
  ],
  "request_id": "uuid",
  "timestamp": "2024-12-20T10:30:00Z"
}
```

---

## 10. バージョニング

### 10.1 APIバージョン
URL内でバージョンを明示

```
/api/v1/plans
/api/v2/plans
```

### 10.2 フィールドの非推奨化
```json
{
  "old_field": "値",
  "old_field_deprecated": true,
  "new_field": "値"
}
```

---

## 11. 実装チェックリスト

### 11.1 開発時チェック項目
- [ ] エンドポイント名が命名規則に従っているか
- [ ] JSONフィールド名がsnake_caseか
- [ ] エラーレスポンスが標準形式か
- [ ] 日時がISO 8601形式か
- [ ] ページネーションが標準パラメータか
- [ ] HTTPステータスコードが適切か

### 11.2 レビュー時チェック項目
- [ ] API設計が一貫しているか
- [ ] 命名が予測可能か
- [ ] ドキュメントが更新されているか
- [ ] 既存APIとの整合性があるか

---

## 12. 参考リンク

- [OpenAPI 3.0 Specification](https://swagger.io/specification/)
- [RESTful API Design Best Practices](https://restfulapi.net/)
- [JSON API Specification](https://jsonapi.org/)
- [ISO 8601 Date Format](https://www.iso.org/iso-8601-date-and-time-format.html) 