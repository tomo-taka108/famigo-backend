# Famigo（ファミゴー）

## サービス名
**Famigo（ファミゴー）**

---

## サービス概要
Famigoは、「なるべくお金をかけずに、子どもとたっぷり遊べる場所」を探しやすくするためのサービスです。  
公園・アスレチック・水遊びなどのスポットを、**エリア / カテゴリ / 予算 / 対象年齢 / 設備（オムツ替え・ベビーカーOK等）**で絞り込み検索できます。

- 未ログインでも：スポット検索・詳細閲覧・レビュー閲覧OK
- ログインすると：お気に入り、レビュー投稿/編集/削除、アカウント設定が使えます

---

## 制作背景
私には２人の小学生の息子がおり、家族そろって楽しめる場所に出かけるのが大好きです。
とは言ってもテーマパーク等のお金がかかる施設に毎週のように行けるわけでもなく、
安くて楽しい良い場所はないか、WEB情報をよく探していました。
ですが、情報はSNSや地図アプリ、ブログなどに散らばっていて、毎回探すのが地味に大変です。
また、子どもと出かけるとなると、「どこ行こう？」より先に **“条件”** が決まってたりします。  
（無料がいい、ベビーカーOKがいい、トイレが気になる、水遊びできる？など）
 
そこで **「家族連れの気になるポイントに寄せた検索」と「リアルな口コミ」** をまとめて見られる場所が欲しくて作りました。

---

## 主な使用技術

### フロントエンド
- React 19 / TypeScript
- Vite
- React Router
- Tailwind CSS（v4）
- lucide-react（アイコン）

### バックエンド
- Java 21
- Spring Boot 3.5.7
- Spring Security（JWT / ステートレス）
- MyBatis
- Flyway（マイグレーション & seed）
- MySQL
- springdoc-openapi（Swagger UI）

### テスト
- JUnit 5
- Spring Boot Test / MockMvc
- Testcontainers（MySQL）
- MyBatis Spring Boot Starter Test

### インフラ（※作成中：仮）
- AWS（ALB / EC2 / RDS / Route53 / ACM）
- フロント：S3 + CloudFront（想定）

---

## 機能一覧

### スポット
- スポット一覧表示（検索条件指定OK）
    - キーワード（スポット名/住所/エリア部分一致）
    - カテゴリ（複数）
    - 予算（FREE / UNDER_1000 / UNDER_2000 / OVER_2000）
    - 対象年齢（ALL / PRESCHOOL / ELE_LOW / ELE_HIGH / JUNIOR_HIGH）
    - 設備（diaper / stroller / playground / athletics / water / indoor）
- スポット詳細表示（基本情報 + 設備情報 + 各種メモ）

### お気に入り（ログイン必須）
- お気に入り登録/解除
- お気に入り一覧表示

### レビュー（ログイン必須）
- レビュー一覧表示（未ログインでも閲覧OK）
- レビュー投稿
- 自分のレビュー編集/削除

### 認証・ユーザー
- 新規登録（登録後、自動ログイン）
- ログイン / ログアウト
- 自分の情報取得（/auth/me）
- アカウント設定（ログイン必須）
    - 表示名変更
    - メール変更（※退会ユーザーのemailも含め、再利用は不可）
    - パスワード変更
    - 退会（論理削除：表示名は「退会ユーザー」に変更、レビューは残す）

---

## デプロイURL
※作成中のため仮です

- フロント（仮）：`https://famigo.example.com`
- API（仮）：`https://api.famigo.example.com`
- Swagger（ローカル）：`http://localhost:8080/swagger-ui/index.html`
    - 本番（prod）では Swagger UI / api-docs は無効化しています

---

## 操作画面
※ここはあとで **動画/GIF** を貼る想定です

- トップ（LP）
    - `docs/demo/landing.gif`（予定）
- スポット検索 → 一覧
    - `docs/demo/search.gif`（予定）
- スポット詳細 → レビュー確認
    - `docs/demo/detail_review.gif`（予定）
- ログイン → お気に入り → 一覧
    - `docs/demo/favorites.gif`（予定）
- アカウント設定（プロフィール更新・パスワード変更・退会）
    - `docs/demo/account.gif`（予定）

---

## API仕様書
- Swagger UI（ローカル開発用）
    - `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON
    - `http://localhost:8080/v3/api-docs`

※本番（prod）では安全のため Swagger は無効化しています。

---

## APIのURL設計

### 認証
| Method | Path | 説明 | 認可 |
|---|---|---|---|
| POST | `/auth/register` | 新規登録（登録後にJWT発行） | GUEST |
| POST | `/auth/login` | ログイン（JWT発行） | GUEST |
| GET | `/auth/me` | 自分のユーザー情報取得 | USER/ADMIN |

### スポット
| Method | Path | 説明 | 認可 |
|---|---|---|---|
| GET | `/spots` | スポット一覧（検索条件指定可） | GUEST |
| GET | `/spots/{id}` | スポット詳細 | GUEST |

**検索クエリ例（フロントから利用）**
- `/spots?keyword=公園&categoryIds=1&price=FREE&age=PRESCHOOL&facilities=diaper&facilities=water`

### カテゴリ
| Method | Path | 説明 | 認可 |
|---|---|---|---|
| GET | `/categories` | カテゴリ一覧 | GUEST |

### 設備（スポット設備）
| Method | Path | 説明 | 認可 |
|---|---|---|---|
| GET | `/spot-facilities/{spotId}` | 設備情報取得 | （現状：ログイン必須扱い） |

※スポット詳細には設備情報を含めて返しているため、基本は詳細APIで足ります（用途に応じて整理予定）

### レビュー
| Method | Path | 説明 | 認可 |
|---|---|---|---|
| GET | `/spots/{spotId}/reviews` | レビュー一覧 | GUEST |
| POST | `/spots/{spotId}/reviews` | レビュー投稿 | USER/ADMIN |
| PUT | `/spots/{spotId}/reviews/{reviewId}` | 自分のレビュー更新 | USER/ADMIN |
| DELETE | `/spots/{spotId}/reviews/{reviewId}` | 自分のレビュー削除 | USER/ADMIN |

### お気に入り
| Method | Path | 説明 | 認可 |
|---|---|---|---|
| GET | `/favorites` | お気に入り一覧 | USER/ADMIN |
| POST | `/spots/{spotId}/favorites` | お気に入り登録 | USER/ADMIN |
| DELETE | `/spots/{spotId}/favorites` | お気に入り解除 | USER/ADMIN |

### ユーザー自己管理（/users/me）
| Method | Path | 説明 | 認可 |
|---|---|---|---|
| PUT | `/users/me/profile` | 表示名 + メールを同時更新（原子性重視） | USER/ADMIN |
| PUT | `/users/me/display-name` | 表示名更新 | USER/ADMIN |
| PUT | `/users/me/email` | メール更新 | USER/ADMIN |
| PUT | `/users/me/password` | パスワード変更（204） | USER/ADMIN |
| DELETE | `/users/me` | 退会（論理削除）（204） | USER/ADMIN |

---

## ER図
※ここはあとで画像を貼る想定です（例：`docs/er.png`）

### テーブル概要（実装済み）
- `users`（ユーザー / 管理者、論理削除あり）
- `categories`（カテゴリマスタ）
- `spots`（スポット本体、カテゴリに紐づく、論理削除あり）
- `spot_facilities`（スポット設備：spotsと1:1、論理削除あり）
- `reviews`（レビュー：spotsとusersに紐づく、論理削除あり）
- `favorites`（お気に入り：users×spotsの中間、論理削除あり）

---

## 画面遷移図
※別で作成して貼り付け予定（例：`docs/ui-flow.png`）

---

## シーケンス図
※別で作成して貼り付け予定（例：`docs/sequence-review-post.png`）

---

## インフラ構成図
※別で作成して貼り付け予定（例：`docs/infra.png`）  
（現在AWSに載せ替え中で、URLや構成は整理しながら更新予定です）

---

## 自動テスト

### バックエンド（実装済み）
- DTOバリデーションテスト
    - `RegisterRequestTest` / `UpdateUserMeRequestTest`
- Mapper層（MySQL実DBで検証）
    - Testcontainersで `mysql:8.0.42` を起動
    - Flyway migration/seed を流してからテスト
    - `UserMapperTest` / `SpotMapperTest` / `ReviewMapperTest` / `FavoriteMapperTest` など
- Service層テスト
    - `AuthServiceTest` / `ReviewServiceTest` / `UserSelfServiceTest` など
- Controller層テスト（MockMvc）
    - `SpotControllerTest` / `AuthControllerTest` / `ReviewControllerTest` など
    - ※コントローラのテストではフィルタを無効化して、リクエスト/レスポンス中心に確認

---

## 工夫した点・苦労した点

- **JWT + ステートレス構成**
    - セッションを使わず、`Authorization: Bearer <token>` で認証
    - 未ログインでも閲覧できる範囲を明確化（GUEST権限を付与）

- **エラーの返し方を統一**
    - バックエンドは `errorCode + message (+ fieldErrors)` の形で返却
    - フロントは `errorCode` を見て日本語表示に寄せやすい設計にしました

- **本番設定の事故を防ぐ**
    - prod起動時に `JWT_SECRET` が未設定/テンプレ値/短すぎる場合、起動を落として検知するようにしました

- **プロフィール更新の“部分更新”を防止**
    - 表示名とメールをまとめて更新できる `/users/me/profile` を用意
    - バリデーションやUNIQUE制約で失敗したら、更新は確定しない（トランザクション）

- **DBはFlywayで管理**
    - 初期スキーマ + seed データ（カテゴリ/スポット/設備/ユーザー/レビュー）を migration として管理
    - デモアカウントも migration に含めて、検証しやすくしています

- **テストはMySQL実DBで回す**
    - H2差異を避けたかったので、MapperはTestcontainers(MySQL)で検証しています

---

## 今後の展望
- 管理者機能の拡充（スポット追加/編集、ユーザー管理 など）
- お気に入りやレビューのUI改善（並び替え、フィルタ、編集導線の強化）
- 画像投稿（スポット/レビュー）や地図表示の強化
- CI/CD（GitHub Actions）でテスト〜デプロイを自動化
- 退会後の扱い（メール再利用方針など）は、運用に合わせて再検討

---

## デモアカウント（ローカル用 / seed）
※DBをFlywayで作成すると投入されます

- USER
    - `test1@example.com` / `test1234`
    - `test2@example.com` / `test2345`
    - `demo_user@example.com` / `demo1234`
- ADMIN
    - `admin@example.com` / `admin1234`
    - `demo_admin@example.com` / `admin1234`
