@import dev.sivalabs.musings.config.ViewHelpers
!{var csrf = ViewHelpers.csrfToken();}
@if(csrf != null)<input type="hidden" name="${csrf.getParameterName()}" value="${csrf.getToken()}"/>@endif
