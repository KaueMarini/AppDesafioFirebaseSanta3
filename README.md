rules_version = '2';

service cloud.firestore {
  match /databases/{database}/documents {

    // Regra para a coleção 'usuarios'
    match /usuarios/{userId} {
      // Permite o cadastro (create)
      // Permite que usuários logados leiam a lista de restaurantes (list)
      allow create, list: if request.auth != null;
      
      // Só o dono do documento pode ler ou atualizar seus próprios dados
      allow read, update: if request.auth != null && request.auth.uid == userId;
    }
    
    // Regra para a coleção 'pedidos'
    match /pedidos/{pedidoId} {
      // Qualquer usuário logado pode criar um pedido
      allow create: if request.auth != null;
      
      // Um usuário pode ler um pedido SE:
      // 1. for o seu próprio pedido (o ID do usuário bate)
      // 2. ou se for o restaurante dono do pedido (o ID do restaurante bate)
      allow read: if request.auth != null && 
                  (request.auth.uid == resource.data.usuarioId || 
                   request.auth.uid == resource.data.restauranteId);
                   
      // Só o restaurante que recebeu o pedido pode atualizá-lo (mudar status)
      allow update: if request.auth != null && 
                     request.auth.uid == resource.data.restauranteId;
      
      // Ninguém pode deletar pedidos (para manter o histórico)
      allow delete: if false;
    }
  }
}
