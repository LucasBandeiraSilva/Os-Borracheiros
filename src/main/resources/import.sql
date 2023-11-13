insert into tipo_users (nome) values("ADMIN")
insert into tipo_users (nome) values("ESTOQUISTA")

/*
EM CASO DE BUG EM QUE NÃO EXISTA MAIS O USER ADMIN OU ESTOQUISTA NO SEU SISTEMA É PQ HOUVE TROCA DE BANCO DE DADOS
E A PRINCIPIO ELE RESETA TUDO TODA VEZ QUE TROCA O BANCO, LEMBRANDO QUE VOCÊS TERÃO QUE CADASTRAR PRODUTOS DE NOVO 
SO ALERTAR QUE A IMAGEM NÃO PODE SER MUITO GRANDE POIS DA UM BUG NO SISTEMA
SEGUE ABAIXO OS SCRIPTS!!!


Usuario estoquista
 insert into usuarios (role_id, cpf,email,senha,status_usuario,nome) values(2,"13804245005","jose@teste","jose123",1,"Jose");

Usuario Admin 
  insert into usuarios (role_id, cpf,email,senha,status_usuario,nome) values(1,"43362644066","joao@teste","joao",1,"joao");
*/
