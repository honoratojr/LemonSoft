document.getElementById(`btnNovaImagem`).onclick = function() {
    let qtdFieldset = document.querySelectorAll('#inputImagem > fieldset').length;
    document.getElementById('inputImagem').insertAdjacentHTML('beforeend',
    `
    <fieldset>
        <div class="row flex-center" style="padding: 20px 0px">
            <img id="selectedImage${qtdFieldset}" onclick="document.getElementById('incluirImagem${qtdFieldset}').click()" src="/img/add.svg" style="width: 200px; cursor: pointer;" />
            <input class="form-control d-none" id="incluirImagem${qtdFieldset}" type="file" name="arquivo" onchange="displaySelectedImage(event, 'selectedImage${qtdFieldset}')" accept="image/*"/>
            <div class="col-sm-3">
                <label for="txtOrdenacao${qtdFieldset}"  class="form-label">Ordem</label>
                <input type="number" class="form-control" value="${qtdFieldset+1}" id="txtOrdenacao${qtdFieldset}" name="imagens[${qtdFieldset}].ordenacao" />
            </div>
            <div class="col-sm-3">
                <label for="txtOrdenacao${qtdFieldset}"  class="form-label" >Principal: </label>
                <input class="form-check-input" type="checkbox" role="switch" id="flexSwitchCheckChecked">            
            </div>
        </div>
    </fieldset>
    `);
}

function displaySelectedImage(event, elementId) {
    const selectedImage = document.getElementById(elementId);
    const fileInput = event.target;

    if (fileInput.files && fileInput.files[0]) {
        const reader = new FileReader();

        reader.onload = function (e) {
            selectedImage.src = e.target.result;
        };

        reader.readAsDataURL(fileInput.files[0]);
    }
}