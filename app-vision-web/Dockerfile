# Imagem base com Node
FROM node:20-alpine

# Diretório de trabalho dentro do container
WORKDIR /app

# Copia apenas os arquivos de dependência primeiro (melhor cache)
COPY package*.json ./

# Instala as dependências (inclui devDeps, pois vamos rodar npm run dev)
RUN npm install

# Copia o restante do projeto
COPY . .

# Porta padrão do Vite
EXPOSE 5173

# Sobe o Vite dev server acessível externamente
CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0", "--port", "5173"]
