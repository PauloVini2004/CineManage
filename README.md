# Cine-Manager Simulator

![GitHub repo size](https://img.shields.io/github/repo-size/iuricode/README-template?style=for-the-badge)
![GitHub language count](https://img.shields.io/github/languages/count/iuricode/README-template?style=for-the-badge)
![GitHub forks](https://img.shields.io/github/forks/iuricode/README-template?style=for-the-badge)
![Bitbucket open issues](https://img.shields.io/bitbucket/issues/iuricode/README-template?style=for-the-badge)
![Bitbucket open pull requests](https://img.shields.io/bitbucket/pr-raw/iuricode/README-template?style=for-the-badge)

<img src="assets/imagem.png" alt="Exemplo imagem">

> Sinta-se como se você realmente estivesse indo comprar ingressos de cinema em um quiosque de autoatendimento.

## 💻 Entregas

O projeto ainda está em desenvolvimento e as próximas atualizações serão voltadas para as seguintes tarefas:

- [x] Entrega 1: Classes básicas (models) implementadas.
- [x] Entrega 2: Implementação inicial com funcionalidades prontas.
- [x] Entrega 3: Protótipo funcional com GUI.
- [x] Entrega Final: Apresentação de Projetos.


## 🤝 Contribuidores

* Gabriel Maksimus Ferreira dos Santos | https://github.com/gabrielmaksimus-ui
* Paulo Vinicius Lins Martins | https://github.com/PauloVini2004
* Arthur Vinicius Flor Jordão Barbosa | https://github.com/Arthurdevvv
* Julia Ellen Felix dos Santos Silva | https://github.com/JUS0110
* Gededias Farias de Aguiar | https://github.com/Gede34games

## Requisitos Funcionais

### 1. Gestão de Catálogo e Salas
- **REQ01**: Cadastrar filmes com título, sinopse, duração e gênero.✅
- **REQ02**: Gerenciar classificação indicativa de cada filme.✅
- **REQ03**: Cadastrar salas com identificação e capacidade total de assentos.✅
- **REQ04**: Implementar herança para salas: Comum, VIP e IMAX.✅

### 2. Programação de Sessões
- **REQ05**: Criar sessões associando filme, sala, horário e idioma.✅
- **REQ06**: Gerenciar o mapa de assentos (composição) específico para cada sessão.✅
- **REQ07**: Atualizar status da sessão (aberta, em exibição, encerrada).✅

### 3. Bilheteria e Bomboniere
- **REQ08**: Registrar venda de ingressos com escolha de assentos no mapa.✅
- **REQ09**: Aplicar regras de meia-entrada conforme categorias legais.✅
- **REQ10**: Cadastrar produtos da bomboniere com controle de estoque.✅
- **REQ11**: Registrar vendas de itens de consumo vinculados ou não ao ingresso.✅

### 4. Relatórios e Estatísticas
- **REQ12**: Gerar relatório de bilheteria por filme com taxa de ocupação.✅
- **REQ13**: Gerar relatório de vendas da bomboniere por período.✅
- **REQ14**: Exportar faturamento diário em CSV.✅
- **REQ15**: Listar assentos com maior frequência de ocupação.✅

### 5. Alertas e Notificações
- **REQ16**: Notificar o gerente sobre filmes com baixa procura histórica.✅
- **REQ17**: Alertar sobre estoque baixo de itens da bomboniere (pipoca/xarope).✅
- **REQ18**: Enviar confirmação de compra para o e-mail do cliente.✅

### 6. Regras e Restrições
- **REQ19**: **Não permitir a venda** de ingressos para menores de idade sem acompanhante caso a classificação seja restritiva.✅
- **REQ20**: **Bloquear a criação** de sessões com sobreposição de horário na mesma sala.✅
- **REQ21**: **Impedir a venda** de assentos já marcados como ocupados.✅
- **REQ22**: **Validar** estoque antes de confirmar a venda de produtos da bomboniere.✅
- **REQ23**: **Não permitir o cancelamento** de ingressos após o início da sessão.✅
- **REQ24**: **Bloquear alteração** de filme em sessões com vendas já realizadas.✅

## Possíveis APIs/Bibliotecas
- **JavaFX** – Mapa de assentos.
- **iText** – Impressão de ingressos.
- **JUnit** – Testes de regras de negócio.

## 📝 Licença

Esse projeto está sob licença. Veja o arquivo [LICENÇA](LICENSE) para mais detalhes.
