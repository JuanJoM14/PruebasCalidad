function fn() {
  var env = karate.env; // obtener el ambiente desde línea de comandos: -Dkarate.env=qa
  karate.log('karate.env system property was:', env);

  if (!env) {
    env = 'dev';
  }

  var config = {
    env: env,
    baseUrl: 'http://localhost:8080',
    apiVersion: 'v1'
  };

  // Configuraciones específicas por ambiente
  if (env == 'dev') {
    config.baseUrl = 'http://localhost:8080';
  } else if (env == 'qa') {
    config.baseUrl = 'http://qa-server:8080';
  } else if (env == 'prod') {
    config.baseUrl = 'http://prod-server:8080';
  }

  karate.configure('connectTimeout', 10000);
  karate.configure('readTimeout', 30000);

  return config;
}