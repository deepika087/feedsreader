1.)  Create a new user
URL : https://shielded-plateau-45045.herokuapp.com/user/deepika
curl -X POST \
  https://shielded-plateau-45045.herokuapp.com/user/userDemo \
  -H 'accept: application/json' \
  -H 'cache-control: no-cache' \
  -H 'content-type: text/plain' 

2.) Create a new feed
URL : https://shielded-plateau-45045.herokuapp.com/feed/business
curl -X POST \
  https://shielded-plateau-45045.herokuapp.com/feed/business \
  -H 'accept: application/json' \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' 

3.) Create a new article within a feed
URL : https://shielded-plateau-45045.herokuapp.com/feed/business/article
curl -X POST \
  https://shielded-plateau-45045.herokuapp.com/feed/business/article \
  -H 'accept: application/json' \
  -H 'cache-control: no-cache' \
  -H 'content-type: text/plain' 
  -d 'This is the business demo article'


4.) subscribe a user to a feed
URL : https://shielded-plateau-45045.herokuapp.com/feed/sports/subscribe/userDemo
curl -X POST \
  https://shielded-plateau-45045.herokuapp.com/feed/sports/subscribe/userDemo \
  -H 'accept: application/json' \
  -H 'cache-control: no-cache' 

5.) Unsubcribe a user
URL : https://shielded-plateau-45045.herokuapp.com/feed/sports/unsubscribe/userDemo
curl -X POST \
  https://shielded-plateau-45045.herokuapp.com/feed/sports/unsubscribe/userDemo \
  -H 'accept: application/json' \
  -H 'cache-control: no-cache' \
  -H 'postman-token: 3c9be73d-93b4-9942-e840-06b3aeb9ae07'

5.) View all data of a User
URL : https://shielded-plateau-45045.herokuapp.com/user/userDemo/feeds
curl -X GET \
  https://shielded-plateau-45045.herokuapp.com/user/userDemo/feeds \
  -H 'accept: application/json' \
  -H 'cache-control: no-cache'