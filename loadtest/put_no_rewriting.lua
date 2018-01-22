
wrk.method = "PUT"
counter = 0
ranCounter = math.random(52,117)

request = function()
   local path = "/v0/entity?id=" .. counter
   local_body = string.char(ranCounter)
   for i = 1, 1024 do
      local_body = local_body .. string.char(ranCounter)
   end
   wrk.body = local_body
   counter = counter + 1
   return wrk.format(nil, path)
end