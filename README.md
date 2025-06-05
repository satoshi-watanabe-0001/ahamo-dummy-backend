# ahamo-dummy-backend

オンライン契約システム - バックエンドAPI実装

## 📋 プロジェクト概要

スマートフォン・携帯プランのオンライン契約システムのバックエンドAPI実装です。  
**SCRUM-30** (オンライン契約システム) の一部として、**SCRUM-77** (API仕様統一設計) で策定された仕様に基づいて開発されます。

### 🎯 主な機能
- プラン選択・比較 (3種類)
- 機種選択・検索 (20機種対応)
- オプション選択
- 契約手続き (個人情報、eKYC、電子署名)
- 決済・配送管理
- MNP対応

## 📁 ディレクトリ構成

```
ahamo-dummy-backend/
├── README.md                    # 本ファイル
├── api-specs/                   # API仕様書
│   ├── README.md               # API仕様概要
│   ├── API-naming-conventions.md # 命名規則ガイドライン
│   └── openapi/
│       ├── core-api.yaml       # OpenAPI 3.0 仕様書
│       └── schemas/
│           └── common.json     # 共通JSON Schema
└── (実装ファイルは今後追加)
```

## 🔧 API仕様

完全なAPI仕様書は [`api-specs/`](./api-specs/) ディレクトリに配置されています。

### 主要エンドポイント
- **認証・ユーザー管理**: 3エンドポイント
- **商品管理**: 6エンドポイント (プラン・機種・オプション)
- **契約管理**: 5エンドポイント
- **本人確認(eKYC)**: 2エンドポイント
- **決済**: 2エンドポイント
- **配送**: 2エンドポイント
- **MNP**: 2エンドポイント

**合計**: 21エンドポイントで35機能をカバー

### 技術仕様
- **OpenAPI 3.0**: 標準的なAPI仕様記述
- **RESTful設計**: HTTP メソッドとリソースベースの設計
- **JWT認証**: Bearer Token による認証
- **JSON Schema**: 厳密なデータ検証

## 🚀 開発環境

### 必要なツール
- **API仕様確認**: [Swagger Editor](https://editor.swagger.io/)
- **JSON Schema検証**: [JSON Schema Validator](https://www.jsonschemavalidator.net/)

### 環境URL
```bash
# 開発環境
http://localhost:3000/v1

# ステージング環境
https://api-staging.online-contract.example.com/v1

# 本番環境  
https://api.online-contract.example.com/v1
```

## 📚 ドキュメント

- **[API仕様概要](./api-specs/README.md)**: API全体の概要と機能
- **[OpenAPI仕様書](./api-specs/openapi/core-api.yaml)**: 完全なAPI仕様
- **[命名規則ガイドライン](./api-specs/API-naming-conventions.md)**: 実装時の命名規則
- **[共通JSON Schema](./api-specs/openapi/schemas/common.json)**: データ形式定義

## 🤝 開発チーム

- **バックエンドAPI**: バックエンドチーム
- **フロントエンド統合**: フロントエンドチーム
- **API設計レビュー**: アーキテクトチーム

## 📄 ライセンス

MIT License - 社内プロジェクト用

---

**関連チケット**
- **SCRUM-30**: オンライン契約システム (親エピック)
- **SCRUM-77**: API仕様統一設計 (本実装の基盤)