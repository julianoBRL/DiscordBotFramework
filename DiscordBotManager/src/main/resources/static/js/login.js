document.addEventListener('DOMContentLoaded', function () {
      const form = document.getElementById('loginForm');
      const submitBtn = document.getElementById('submitBtn');
      const btnSpinner = document.getElementById('btnSpinner');
      const alertPlaceholder = document.getElementById('alertPlaceholder');

      function showAlert(message, type = 'danger') {
        alertPlaceholder.innerHTML = '';
        const wrapper = document.createElement('div');
        wrapper.innerHTML = [
          `<div class="alert alert-${type} alert-dismissible fade show mt-2" role="alert">`,
          `  ${message}`,
          '  <button type="button" class="btn-close btn-close-white" data-bs-dismiss="alert" aria-label="Close"></button>',
          '</div>'
        ].join('');
        alertPlaceholder.append(wrapper);
      }

      function setLoading(isLoading) {
        if (isLoading) {
          submitBtn.setAttribute('disabled', 'true');
          btnSpinner.classList.remove('d-none');
        } else {
          submitBtn.removeAttribute('disabled');
          btnSpinner.classList.add('d-none');
        }
      }

      form.addEventListener('submit', function (e) {
        e.preventDefault();
        const username = form.username.value.trim();
        const password = form.password.value.trim();
        const remember = form.remember.checked;

        if (!username || !password) {
          showAlert('Por favor, preencha usuário e senha.', 'warning');
          return;
        }

        setLoading(true);

        // Simula requisição de login
        setTimeout(function () {
          setLoading(false);
          // Simulação simples: usuário "admin" e senha "password" -> sucesso
          if (username === 'admin' && password === 'password') {
            showAlert('Login bem-sucedido! Redirecionando...', 'success');
            // Aqui você faria o redirecionamento real
            setTimeout(function () {
              // redirecionamento simulado
              window.location.href = '#';
            }, 900);
          } else {
            showAlert('Usuário ou senha incorretos.', 'danger');
          }
        }, 900);
      });
    });
