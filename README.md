# william-jenkins-infra

Configuração versionada do Jenkins para executar o pipeline do repositório `ProjetoSitee/william-java`.

## Conteúdo

- `Dockerfile`: imagem Jenkins LTS com Java 21, Docker e plugins.
- `plugins.txt`: plugins instalados automaticamente.
- `casc/jenkins.yaml`: segurança e ferramentas via Jenkins Configuration as Code.
- `compose.yaml`: definição reproduzível do serviço.
- `vars/standardPipeline.groovy`: Shared Library reutilizável para aplicações Java, com testes, porta de qualidade e artefatos.

## Segurança

O arquivo `.env` e quaisquer chaves são ignorados pelo Git. Na AWS, os segredos deverão ficar no Jenkins Credentials e/ou AWS Secrets Manager, nunca no GitHub.

O Jenkins não recebe acesso ao socket Docker do computador. O pipeline cria uma imagem OCI em arquivo com Jib, sem daemon privilegiado; futuramente esse estágio poderá publicar diretamente no Amazon ECR.

O contêiner está limitado a 2 GB de memória, com heap máximo de 1 GB para a JVM. O volume do Jenkins usa armazenamento dinâmico do Docker Desktop: o espaço exibido na tela de nós é o espaço disponível no disco virtual, não uma reserva feita pelo Jenkins. A retenção dos dez builds mais recentes limita o crescimento dos artefatos.

## Próxima etapa

Depois de escolher onde o Jenkins será hospedado, conecte um Pipeline Multibranch a:

`https://github.com/ProjetoSitee/william-java.git`

A infraestrutura AWS será adicionada separadamente para manter criação e custos explícitos.

## Uso na aplicação

O Jenkinsfile da aplicação importa `william-devops-pipelines` e chama `standardPipeline` com Java 21, Spring Boot e criação de imagem habilitados. A lógica comum permanece centralizada neste repositório. O estágio `Testes e qualidade` executa `mvn clean verify`, publica os resultados JUnit e arquiva o relatório JaCoCo.
