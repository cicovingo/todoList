Todo Uygulaması (Spring Boot & Angular)
Bu proje, Spring Boot ile geliştirilmiş bir RESTful API backend'i ve Angular ile oluşturulmuş bir frontend'i içeren basit bir Todo List uygulamasıdır. Kullanıcılar todo ekleyebilir, listeleyebilir, güncelleyebilir ve silebilirler.

İçindekiler
Proje Yapısı

Gereksinimler

Kurulum ve Çalıştırma

Doğrudan Çalıştırma

Backend (Spring Boot)

Frontend (Angular)

Docker ile Çalıştırma

Docker Kurulumu

Backend için Dockerfile

Frontend için Dockerfile

Docker Compose ile Çalıştırma

API Dokümantasyonu

Kullanım

Ek Bilgiler


Proje Yapısı
Proje iki ana dizine ayrılmıştır:

main/: Spring Boot RESTful API'sini içerir.

frontend-new/: Angular kullanıcı arayüzünü içerir.

Gereksinimler
Her iki bölümü de çalıştırmak için aşağıdaki yazılımların sisteminizde yüklü olması gerekmektedir:

Java 17 veya üzeri (Backend için)

Maven 3.6.x veya üzeri (Backend için)

Node.js 18.x veya üzeri (Frontend için)

Angular CLI 18.x veya üzeri (Frontend için)

Docker Desktop (Docker ile çalıştırmak isterseniz)

Kurulum ve Çalıştırma
Doğrudan Çalıştırma
Backend (Spring Boot)
Terminali açın ve main dizinine gidin:

Bash

cd main
Maven kullanarak projeyi derleyin (build):

Bash

mvn clean install
Bu komut target dizini altında bir .jar dosyası oluşturacaktır.

Uygulamayı çalıştırın:

Bash

java -jar target/todo-0.0.1-SNAPSHOT.jar
veya Spring Boot Maven eklentisi ile:

Bash

mvn spring-boot:run
Backend varsayılan olarak http://localhost:8080 adresinde çalışacaktır. SQLite veritabanı (todos.db) otomatik olarak data/ dizininde oluşturulacaktır.

Frontend (Angular)
Yeni bir terminal açın ve frontend-new dizinine gidin:

Bash

cd frontend-new
Node.js bağımlılıklarını yükleyin:

Bash

npm install
Angular uygulamasını başlatın:

Bash

ng serve --open
Bu komut uygulamayı derleyecek ve varsayılan olarak http://localhost:4200 adresinde tarayıcınızda açacaktır.

Docker ile Çalıştırma
Docker ile çalıştırmak, uygulamayı bağımsız ve taşınabilir kapsayıcılarda çalıştırmanızı sağlar.

Docker Kurulumu
Sisteminizde Docker Desktop'ın kurulu olduğundan emin olun.
Docker Desktop Kurulumu

projenin ana dizinine gelin aşağıdaki komutu çalıştırın
docker-compose up --build -d
Bu komut Docker imajlarını derleyecek ve servisleri arka planda başlatacaktır (-d).

Uygulamalar çalışmaya başladığında, tarayıcınızdan http://localhost:4200 adresine erişebilirsiniz.

Servisleri durdurmak için:

Bash

docker-compose down
API Dokümantasyonu
Backend uygulaması çalışırken, Swagger UI aracılığıyla API dokümantasyonuna erişebilirsiniz:

Swagger UI: http://localhost:8080/swagger-ui.html

Kullanım
Uygulama açıldığında:

Metin kutusuna bir todo yazın ve Enter tuşuna basın listeye eklenir.

Listede yer alan bir todo'yu tamamlandı olarak işaretlemek için yanındaki kutuya tıklayın.

Listede yer alan bir todo'yu düzenlemek için ilgili todo kaydına tıklayın ve kayıt ekranındaki title alanını güncelleyip enter butonuna basın.

Bir todo'yu silmek için yanındaki delete butonuna tıklayın.

Ek Bilgiler
Veritabanı: Uygulama, geliştirme ortamında SQLite ve MongoDb kullanır. Veritabanı dosyası data/todos.db konumunda bulunur. MongoDb de kurulumu yapılıp application.properties de yer alan konfigürasyonlar yapılır.
Docker çalışmasının takibi ve bazı ekran görüntüleri de projede mevcuttur.

CORS Politikası: Backend, Angular uygulamasının varsayılan adresi olan http://localhost:4200 adresinden gelen isteklere izin verecek şekilde yapılandırılmıştır.

Hata Ayıklama: Geliştirme sırasında herhangi bir sorunla karşılaşırsanız, backend ve frontend konsol çıktılarını dikkatlice inceleyin.