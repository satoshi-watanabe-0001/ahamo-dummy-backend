# オンライン契約システム API仕様書

**SCRUM-77**: API仕様統一設計 - フロント・バックエンド間インターフェース詳細設計  
**Parent Epic**: SCRUM-30 - オンライン契約システム  
**作成日**: 2024年12月  
**バージョン**: 1.0.0

---

## 📋 プロジェクト概要

スマホ・携帯プランのオンライン契約システムにおけるフロントエンドとバックエンド間のAPI仕様を統一設計しました。35の機能（フロントエンド22個 + バックエンド13個）に対応する包括的なAPI仕様を提供します。

### 🎯 目標
- **API仕様の統一**: 一貫性のあるRESTful API設計
- **データ形式の統一**: JSON Schemaによる厳密なデータ検証
- **エラーハンドリング標準化**: 統一的なエラーレスポンス
- **セキュリティ仕様**: 認証・認可の標準化
- **外部API連携**: 決済・配送・SMS・eKYC統合

---

## 🗂️ ファイル構成

```
api-specs/
├── README.md                          # 本ファイル
├── API-naming-conventions.md          # 命名規則ガイドライン
├── openapi/
│   ├── core-api.yaml                 # メインAPI仕様（OpenAPI 3.0）
│   └── schemas/
│       └── common.json               # 共通JSON Schema定義
└── docs/                             # ドキュメント（将来追加予定）
```

---

## 🔧 API機能概要

### **認証・ユーザー管理**
- ユーザーログイン・トークン管理
- プロフィール情報管理

### **商品管理**
- **プラン管理**: 3種類のプランの選択・比較
- **機種管理**: 20機種の検索・フィルタリング・在庫確認
- **オプション管理**: 保険・アクセサリー・追加サービス

### **契約処理**
- 契約の作成・更新・確定
- 途中保存機能
- 契約ステータス管理

### **本人確認（eKYC）**
- eKYCセッション管理
- 本人確認書類処理
- 認証状況の追跡

### **決済処理**
- 複数決済方法対応（クレジットカード・コンビニ・銀行振込）
- 決済状況の管理

### **配送管理**
- 配送先住所登録・管理
- 配送状況の追跡

### **MNP（番号ポータビリティ）**
- MNP利用可能性確認
- 番号移行手続き管理

---

## 🚀 技術仕様

### **API設計原則**
- **RESTful設計**: HTTP メソッドとリソースベースの設計
- **OpenAPI 3.0**: 標準的なAPI仕様記述
- **JSON形式**: すべての通信でJSON使用
- **統一的な命名規則**: snake_case による一貫した命名

### **認証・セキュリティ**
- **JWT認証**: Bearer Token による認証
- **API Key認証**: システム間通信用
- **HTTPS通信**: すべての通信でSSL/TLS必須

### **データ形式**
- **JSON Schema**: 厳密なデータ検証
- **ISO 8601**: 日時形式の標準化
- **UUID v4**: 一意識別子の標準

### **エラーハンドリング**
- **統一エラー形式**: 一貫したエラーレスポンス構造
- **詳細エラー情報**: フィールドレベルの検証エラー
- **リクエストID**: トレーサビリティの確保

---

## 📊 対応機能マトリクス

| 機能カテゴリ | エンドポイント数 | 主要機能 |
|--------------|------------------|----------|
| 認証・ユーザー | 3 | ログイン、トークン管理、プロフィール |
| プラン管理 | 2 | プラン一覧・詳細取得 |
| 機種管理 | 3 | 機種検索・詳細・在庫確認 |
| オプション管理 | 1 | オプション一覧取得 |
| 契約管理 | 4 | 契約CRUD、確定処理 |
| eKYC | 2 | セッション管理、状況確認 |
| 決済 | 2 | 決済方法・処理 |
| 配送 | 2 | 住所管理 |
| MNP | 2 | 利用可能性確認、申請 |
| **合計** | **21** | **35機能をカバー** |

---

## 🔗 API エンドポイント一覧

### 認証・ユーザー管理
```
POST   /auth/login                    # ユーザーログイン
POST   /auth/refresh                  # トークンリフレッシュ
GET    /users/profile                 # プロフィール取得
```

### 商品管理
```
GET    /plans                         # プラン一覧
GET    /plans/{planId}                # プラン詳細
GET    /devices                       # 機種一覧
GET    /devices/{deviceId}            # 機種詳細
GET    /devices/{deviceId}/inventory  # 在庫確認
GET    /options                       # オプション一覧
```

### 契約管理
```
POST   /contracts                     # 契約作成
GET    /contracts                     # 契約一覧
GET    /contracts/{contractId}        # 契約詳細
PUT    /contracts/{contractId}        # 契約更新
POST   /contracts/{contractId}/submit # 契約確定
```

### 本人確認
```
POST   /ekyc/session                  # eKYCセッション開始
GET    /ekyc/session/{sessionId}/status # eKYC状況確認
```

### 決済
```
GET    /payments/methods              # 決済方法一覧
POST   /payments/process              # 決済処理
```

### 配送
```
GET    /shipping/addresses            # 配送先一覧
POST   /shipping/addresses            # 配送先登録
```

### MNP
```
POST   /mnp/eligibility               # MNP利用可能性確認
POST   /mnp/request                   # MNP申請
```

---

## 📝 データモデル主要項目

### Contract（契約）
```json
{
  "id": "uuid",
  "status": "draft|pending_ekyc|pending_payment|active|cancelled",
  "plan": { Plan },
  "device": { Device },
  "options": [ Option ],
  "customer_info": { CustomerInfo },
  "total_amount": 127780.00,
  "created_at": "2024-12-20T10:30:00Z"
}
```

### Device（機種）
```json
{
  "id": "device_iphone15_001",
  "name": "iPhone 15",
  "category": "iPhone|Android",
  "price_range": "entry|mid|premium",
  "price": 124800.00,
  "colors": ["スターライト", "ブルー"],
  "in_stock": true
}
```

### Plan（プラン）
```json
{
  "id": "plan_basic_001",
  "name": "ベーシックプラン",
  "monthly_fee": 2980.00,
  "data_capacity": "20GB",
  "voice_calls": "無制限"
}
```

---

## 🛠️ 開発環境設定

### 必要なツール
- **OpenAPI仕様確認**: [Swagger Editor](https://editor.swagger.io/)
- **JSON Schema検証**: [JSON Schema Validator](https://www.jsonschemavalidator.net/)
- **API テスト**: Postman、Insomnia、またはcURL

### ローカル開発サーバー
```bash
# 開発用サーバーURL
http://localhost:3000/v1

# ステージング環境
https://api-staging.online-contract.example.com/v1

# 本番環境
https://api.online-contract.example.com/v1
```

---

## ✅ 受入条件

### ✅ 完了済み
- [x] OpenAPI 3.0による35機能すべての仕様策定
- [x] JSON Schemaによる一貫したデータ構造定義
- [x] 統一的なエラーハンドリング仕様
- [x] セキュリティ要件の反映
- [x] 外部API連携（決済・配送・SMS・eKYC）の設計
- [x] API命名規則ガイドラインの策定

### 🔄 次のステップ（実装フェーズ）
- [ ] モックサーバーの構築
- [ ] チームレビューの実施
- [ ] フロントエンド・バックエンドへの仕様共有
- [ ] 実装ガイドラインの作成

---

## 📚 関連ドキュメント

- **[API命名規則ガイドライン](./API-naming-conventions.md)**: 命名規則の詳細
- **[OpenAPI仕様書](./openapi/core-api.yaml)**: 完全なAPI仕様
- **[共通JSON Schema](./openapi/schemas/common.json)**: データ形式定義
- **SCRUM-30要件定義書**: 親エピックの詳細要件

---

## 🤝 貢献・フィードバック

### チーム
- **API設計**: バックエンドチーム
- **フロントエンド統合**: フロントエンドチーム  
- **レビュー**: アーキテクトチーム
- **QA**: テストチーム

### 連絡先
- **技術的質問**: api-support@example.com
- **仕様変更要求**: product-owner@example.com
- **緊急対応**: on-call@example.com

---

## 📄 ライセンス

MIT License - 社内利用のための技術仕様書

---

**更新履歴**
- v1.0.0 (2024-12-20): 初版リリース - SCRUM-77対応 