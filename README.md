# NewsPipeline
📰 NewsPipeline - ETL de flux d’actualités basé sur Kafka et Docker
🚀 Présentation
NewsPipeline est une solution ETL conteneurisée permettant de récupérer, transformer et stocker des données issues de l'API News API. Elle repose sur une architecture microservices orchestrée via Docker Compose, et utilise Apache Kafka pour la gestion de flux de données en temps réel.

Le but est de permettre une collecte automatisée et scalable de données d’actualités, avec un traitement asynchrone et une persistance dans une base MongoDB pour des analyses ultérieures.

🛠️ Architecture
lua
Copier
Modifier
+-------------+          +------------+         +-------------+         +-------------+
|  Aggregator | ───────► | Kafka Pub  | ─────►  | Kafka Topic | ─────►  | Topic-sub   |
| (Data Fetch)|          | (Producer) |         | (Stream)    |         | (Consumer)  |
+-------------+          +------------+         +-------------+         +-------------+
                                                                              │
                                                                              ▼
                                                                       +-------------+
                                                                       |   MongoDB   |
                                                                       +-------------+
Description des composants
Aggregator : Récupère les données depuis l’API News API et les structure selon un modèle prédéfini.

Kafka Publisher (AggCloudKafka) : Publie les données formatées sur des topics Kafka spécifiques.

Kafka : Gère les flux de messages entre les producteurs et consommateurs. Assure la fiabilité et la scalabilité.

Topic-sub (Consumer) : Consomme les données Kafka et les enregistre dans MongoDB.

MongoDB : Stocke les données persistées, prêtes pour l’analyse ou la visualisation.

Tous les composants sont conteneurisés à l’aide de Docker et orchestrés via Docker Compose.

⚙️ Technologies utilisées
🐳 Docker / Docker Compose

🔁 Apache Kafka

📡 News API (https://newsapi.org)

🧩 Python (scripts d’ETL)

🗄️ MongoDB

🧱 Microservices architecture

📦 Installation et déploiement
1. Pré-requis
Docker : Installation officielle

Docker Compose : Guide d’installation

2. Cloner le projet
bash
Copier
Modifier
git clone https://github.com/ton-repo/news-pipeline.git
cd news-pipeline
3. Configuration
Créer un fichier .env à la racine avec :

env
Copier
Modifier
NEWS_API_KEY=your_api_key
MONGO_URI=mongodb://mongo:27017/news
Vérifier les paramètres de connexion dans les fichiers docker-compose.yml et config/*.json.

4. Lancer le projet
bash
Copier
Modifier
docker-compose up --build
Cela lancera :

Kafka + Zookeeper

MongoDB

Aggregator

Topic-sub

5. Vérification
Vérifiez les logs Docker : docker-compose logs -f

Confirmez la collecte et le stockage des données en accédant à MongoDB ou en inspectant les topics Kafka.

🧪 Tester la solution
Lancer manuellement le script Aggregator pour simuler une collecte.

Vérifier que les données apparaissent dans les topics Kafka.

Vérifier que le consumer les insère correctement dans MongoDB.

📁 Structure du projet
bash
Copier
Modifier
.
├── aggregator/           # Service d’ingestion
├── consumer/             # Service de consommation Kafka
├── docker-compose.yml    # Orchestration Docker
├── kafka/                # Configuration Kafka/Zookeeper
├── mongodb/              # Données MongoDB (volumes, init)
├── config/               # Fichiers de config
└── README.md
✨ À venir / TODO
Intégration d’un dashboard de visualisation (ex: Metabase, Grafana)

Monitoring avec Prometheus / Grafana

Tests unitaires pour les microservices

Déploiement sur Kubernetes (en option)

📄 Licence
Ce projet est open source, sous licence MIT.
