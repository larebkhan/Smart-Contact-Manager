

//     // Your script here
//     console.log("This is script file")
//     console.log("This vxfsbfx csd file")
//     $(document).ready(function() {
// const toggleSidebar = () => {

//     console.log("Sidebar toggled!");
//     if($(".sidebar").is(".visible")){
//         //true
//         //band krna hai
        
//         $(".sidebar").css("display","none");
//         $(".content").css("margin-left","0%");
//     }else{
        
//         $(".sidebar").css("display","block");
//         $(".content").css("margin-left","20%");
//     }
// };
//     });

function toggleSidebar() {
    
    const sidebar =  document.getElementsByClassName("sidebar")[0];
    const content =  document.getElementsByClassName("content")[0];

    if(window.getComputedStyle(sidebar).display === "none"){
        sidebar.style.display = "block";
        
        content.style.marginLeft = "20%";
    }
    else{
        sidebar.style.display = "none";
        
        content.style.marginLeft = "0%";
    }
};

const search = () => {
    // console.log("searching...")
    let query= $("#search-input").val();
    
    if(query==''){
        $(".search-result").hide();
    }else{
        console.log(query);

        //sending request to backend
        let url = `http://localhost:8080/search/${query}`

        fetch(url).then((response) =>{
            return response.json();
        }).then((data) =>{
            //data....
            console.log(data);

            let text=`<div class='list-group'>`;

            data.forEach(contact => {
                text+=`<a href='/user/${contact.cId}/contact' class='list-group-item list-group-item-action'>${contact.name}</a>`
            });

            text+=`</div>`;

            $(".search-result").html(text);
            $(".search-result").show();
            
        });




        $(".search-result").show();
    }
};


