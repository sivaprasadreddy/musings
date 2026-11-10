@import org.springframework.validation.Errors
@param Errors errors
@param String field
@if(errors != null && errors.hasFieldErrors(field))
    <p class="mt-1 text-sm text-red-600">@for(var error : errors.getFieldErrors(field))${error.getDefaultMessage()}@if(!error.equals(errors.getFieldErrors(field).getLast()))<br/>@endif@endfor</p>
@endif
