<template lang="pug">
  // Container de fundo preto
  .container
    .section-medalhas
      h2 Sistema de Medalhas
      p.desc Conquiste medalhas respondendo pesquisas e alcançando objetivos

      .grid
        //- Card 1
        .card
          img.icon(src="https://cdn-icons-png.flaticon.com/512/922/922699.png")
          .content
            h3 Primeira Pesquisa
            p Completou sua primeira pesquisa
            .footer
              i.fa.fa-clock-o
              span 14/01/2024

        //- Card 2
        .card
          img.icon(src="https://cdn-icons-png.flaticon.com/512/1828/1828884.png")
          .content
            h3 Participante Ativo
            p Respondeu 5 pesquisas consecutivas
            .footer
              i.fa.fa-clock-o
              span 05/03/2024

        //- Card 3
        .card
          img.icon(src="https://cdn-icons-png.flaticon.com/512/1365/1365686.png")
          .content
            h3 Resposta Rápida
            p Completou pesquisas em menos de 3 minutos
            .footer
              i.fa.fa-clock-o
              span 12/04/2024

        //- Card 4
        .card
          img.icon(src="https://cdn-icons-png.flaticon.com/512/190/190411.png" class="icon-colorful")
          .content
            h3 Campeão de Participação
            p 100% de participação no trimestre
            .footer
              i.fa.fa-clock-o
              span 30/06/2024

        //- Card 5
        .card
          img.icon(src="https://cdn-icons-png.flaticon.com/512/3643/3643566.png" class="icon-colorful")
          .content
            h3 Feedback Completo
            p Respondeu todas as perguntas abertas
            .footer
              i.fa.fa-clock-o
              span 14/01/2024

        //- Card 6 (bloqueado)
        .card.locked
          img.icon(src="https://cdn-icons-png.flaticon.com/512/190/190414.png" class="icon-colorful")
          .content
            h3 Maratonista
            p Respondeu 20 pesquisas
            .footer
              span Bloqueado
</template>

<script>
export default {
  name: "Medalhas"
}
</script>

<style scoped>
/* Container que ocupa toda a tela */
html, body {
  margin: 0;
  padding: 0;
  overflow: hidden; /* Remove qualquer rolagem */
  width: 100vw;
  height: 100vh;
}

.container {
  background: #161616;
  width: 100vw;   /* Largura da tela */
  height: 100vh;  /* Altura da tela */
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  box-sizing: border-box; /* Para o padding não aumentar a largura/altura */
}


/* Estilos para a seção de medalhas */
.section-medalhas {
  color: #fff;
  font-family: "Inter", sans-serif;
}

.desc {
  margin-top: -5px;
  margin-bottom: 65px;
  opacity: 0.8;
}

.grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 18px;
}

.card {
  background: #151515;
  padding: 58px;
  border-radius: 12px;
  display: flex;
  align-items: flex-start;
  gap: 15px;
  border: 1px solid #222;
}

.card.locked {
  opacity: 0.4;
}

.icon {
  width: 45px;
  height: 45px;
}

.content h3 {
  margin: 0;
  font-size: 17px;
  font-weight: 600;
}

.content p {
  margin: 3px 0 12px 0;
  font-size: 14px;
  opacity: 0.8;
}

.footer {
  gap: 6px;
  font-size: 13px;
  opacity: 0.9;
  margin-right: -3rem;
}
</style>